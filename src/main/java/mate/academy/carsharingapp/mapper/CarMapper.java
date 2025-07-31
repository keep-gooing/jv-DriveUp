package mate.academy.carsharingapp.mapper;

import mate.academy.carsharingapp.config.MapperConfig;
import mate.academy.carsharingapp.dao.car.CarDto;
import mate.academy.carsharingapp.dao.car.CreateCarRequestDto;
import mate.academy.carsharingapp.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarDto toDto(Car car);

    Car toEntity(CreateCarRequestDto requestDto);

    void updateCarFromDto(CreateCarRequestDto dto, @MappingTarget Car entity);
}
