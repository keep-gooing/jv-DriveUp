package carsharingapp.service;

import carsharingapp.dto.payment.PaymentRequestDto;
import carsharingapp.dto.payment.PaymentResponseDto;
import carsharingapp.exception.NotificationException;
import carsharingapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface PaymentService {

    Page<PaymentResponseDto> getPayments(User user, Pageable pageable);

    PaymentResponseDto createPaymentSession(Authentication authentication, PaymentRequestDto dto);

    void paymentSuccess(String sessionId) throws NotificationException;

    void paymentCancel(String sessionId) throws NotificationException;
}
