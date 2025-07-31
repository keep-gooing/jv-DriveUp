package mate.academy.carsharingapp.dao.car;

import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.academy.carsharingapp.model.Car.Type;

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
