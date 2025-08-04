package carsharingapp.controller;

import carsharingapp.dto.payment.PaymentRequestDto;
import carsharingapp.dto.payment.PaymentResponseDto;
import carsharingapp.exception.NotificationException;
import carsharingapp.model.User;
import carsharingapp.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payments management", description = "Endpoints for managing payments")
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentsController {
    private final PaymentService paymentService;

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_MANAGER')")
    @Operation(summary = "Get all payments by user id",
            description = "Get all payments by user id")
    @GetMapping
    public Page<PaymentResponseDto> getPaymentsByUserId(@AuthenticationPrincipal User user,
                                                        Pageable pageable) {
        return paymentService.getPayments(user, pageable);
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Operation(summary = "Create new payment", description = "Create new payment")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDto createPayment(Authentication authentication,
                                            @Valid @RequestBody PaymentRequestDto dto) {
        return paymentService.createPaymentSession(authentication, dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_MANAGER')")
    @Operation(summary = "Handle success payment", description = "Handle success payment")
    @GetMapping("/success/{sessionId}")
    public void handleSuccess(@PathVariable("sessionId") String sessionId)
            throws NotificationException {
        paymentService.paymentSuccess(sessionId);
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_MANAGER')")
    @Operation(summary = "Handle cancel payment", description = "Handle cancel payment")
    @GetMapping("/cancel/{sessionId}")
    public void handleCancel(@PathVariable("sessionId") String sessionId)
            throws NotificationException {
        paymentService.paymentCancel(sessionId);
    }
}
