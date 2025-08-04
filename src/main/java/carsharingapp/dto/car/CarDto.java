package carsharingapp.dto.car;

import carsharingapp.model.Car.Type;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CarDto {
    private Long id;
    private String model;
    private String brand;
    private Type type;
    private int inventory;
    private BigDecimal dailyFee;
}
