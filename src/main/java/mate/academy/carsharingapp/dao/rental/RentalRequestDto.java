package mate.academy.carsharingapp.dao.rental;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RentalRequestDto {
    @NotNull
    private Long carId;

    @NotNull
    private LocalDate rentalDate;

    @NotNull
    private LocalDate returnDate;
}
