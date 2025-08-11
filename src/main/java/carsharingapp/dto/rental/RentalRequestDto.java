package carsharingapp.dto.rental;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RentalRequestDto {
    @NotNull
    @Positive
    private Long carId;

    @NotNull
    private LocalDate rentalDate;

    @NotNull
    private LocalDate returnDate;
}
