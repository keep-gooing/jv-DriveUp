package mate.academy.carsharingapp.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharingapp.dao.payment.PaymentRequestDto;
import mate.academy.carsharingapp.dao.payment.PaymentResponseDto;
import mate.academy.carsharingapp.exception.EntityNotFoundException;
import mate.academy.carsharingapp.exception.NotificationException;
import mate.academy.carsharingapp.mapper.PaymentMapper;
import mate.academy.carsharingapp.model.Payment;
import mate.academy.carsharingapp.model.Rental;
import mate.academy.carsharingapp.model.User;
import mate.academy.carsharingapp.notification.NotificationService;
import mate.academy.carsharingapp.repository.PaymentRepository;
import mate.academy.carsharingapp.repository.RentalRepository;
import mate.academy.carsharingapp.security.CustomUserDetailsService;
import mate.academy.carsharingapp.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final String CURRENCY = "usd";
    private final CustomUserDetailsService customUserDetailsService;
    private final NotificationService notificationService;
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentMapper paymentMapper;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    @Value("${payment.success.url}")
    private String successUrl;
    @Value("${payment.cancel.url}")
    private String cancelUrl;

    @Override
    public Page<PaymentResponseDto> getPayments(User user, Pageable pageable) {
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return paymentRepository.findAll(pageable)
                    .map(paymentMapper::toDto);
        }
        return paymentRepository.findAllByRentalUserId(user.getId(), pageable)
                .map(paymentMapper::toDto);
    }

    @Transactional
    @Override
    public PaymentResponseDto createPaymentSession(Authentication authentication,
                                                   PaymentRequestDto dto) {
        Long userId = customUserDetailsService.getUserIdFromAuthentication(authentication);
        Long rentalId = dto.getRentalId();
        Rental rental = rentalRepository
                .findByUser_IdAndId(userId, rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental don`t found by userId: "
                        + userId));

        BigDecimal amount = calculateAmount(rental, Payment.Type.valueOf(dto.getPaymentType()));
        Stripe.apiKey = stripeSecretKey;
        SessionCreateParams sessionParams = createSessionParams(amount);
        Session session = null;
        try {
            session = Session.create(sessionParams);
        } catch (StripeException e) {
            throw new RuntimeException("Can`t create Stripe Session!");
        }
        Payment payment = new Payment();
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(Payment.Type.valueOf(dto.getPaymentType()));
        payment.setRental(rental);
        payment.setSessionUrl(session.getUrl());
        payment.setSessionId(session.getId());
        payment.setAmountToPay(amount);
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public void paymentSuccess(String sessionId) throws NotificationException {
        Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Payment not found by sessionId: " + sessionId));

        if (isPaymentSessionPaid(sessionId)) {
            payment.setStatus(Payment.Status.PAID);
            paymentRepository.save(payment);
            notificationService.sendPaymentSuccessNotification(payment);
        } else {
            throw new RuntimeException("Payment was not successful for sessionId: " + sessionId);
        }
    }

    @Override
    public void paymentCancel(String sessionId) throws NotificationException {
        Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Payment not found by sessionId: " + sessionId));

        if (payment.getStatus().equals(Payment.Status.PENDING)) {
            notificationService.sendPaymentCancelNotification(payment);
        }
    }

    private BigDecimal calculateAmount(Rental rental, Payment.Type type) {
        BigDecimal baseAmount = rental.getCar().getDailyFee();
        if (type == Payment.Type.FINE) {
            long overdueDays = ChronoUnit.DAYS.between(
                    rental.getReturnDate(), rental.getActualReturnDate());

            long days = Math.max(0, overdueDays);
            return baseAmount.multiply(BigDecimal.valueOf(days));
        }
        return baseAmount;
    }

    private SessionCreateParams createSessionParams(BigDecimal amount) {
        return SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(CURRENCY)
                                                .setUnitAmount(amount
                                                        .multiply(BigDecimal.valueOf(100))
                                                        .longValue())
                                                .setProductData(
                                                        SessionCreateParams
                                                                .LineItem
                                                                .PriceData
                                                                .ProductData
                                                                .builder()
                                                                .setName("Car Rental Payment")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    private boolean isPaymentSessionPaid(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            return "paid".equals(session.getPaymentStatus());
        } catch (StripeException e) {
            throw new RuntimeException("Error retrieving Stripe session: " + sessionId, e);
        }
    }
}
