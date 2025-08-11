package carsharingapp.dto.payment;

import carsharingapp.model.Payment;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentResponseDto {
    private Long id;
    private String sessionUrl;
    private String sessionId;
    private BigDecimal amountToPay;
    private Long rentalId;
    private Payment.Status status;
    private Payment.Type type;
}
