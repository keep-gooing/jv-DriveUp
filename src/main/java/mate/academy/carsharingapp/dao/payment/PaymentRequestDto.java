package mate.academy.carsharingapp.dao.payment;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentRequestDto {
    @Positive
    private Long rentalId;
    private String paymentType;
}
