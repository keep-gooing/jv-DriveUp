package mate.academy.carsharingapp.service;

import mate.academy.carsharingapp.dao.payment.PaymentRequestDto;
import mate.academy.carsharingapp.dao.payment.PaymentResponseDto;
import mate.academy.carsharingapp.exception.NotificationException;
import mate.academy.carsharingapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface PaymentService {

    Page<PaymentResponseDto> getPayments(User user, Pageable pageable);

    PaymentResponseDto createPaymentSession(Authentication authentication, PaymentRequestDto dto);

    void paymentSuccess(String sessionId) throws NotificationException;

    void paymentCancel(String sessionId) throws NotificationException;
}
