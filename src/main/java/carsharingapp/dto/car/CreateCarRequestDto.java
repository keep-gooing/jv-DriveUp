package carsharingapp.dto.car;

import carsharingapp.model.Car.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateCarRequestDto {
    @NotBlank
    private String model;
    @NotBlank
    private String brand;
    @NotNull
    private Type type;
    @Positive
    private int inventory;
    @NotNull
    @Positive
    private BigDecimal dailyFee;
}
