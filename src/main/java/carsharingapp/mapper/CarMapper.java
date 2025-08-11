package carsharingapp.mapper;

import carsharingapp.config.MapperConfig;
import carsharingapp.dto.car.CarDto;
import carsharingapp.dto.car.CreateCarRequestDto;
import carsharingapp.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarDto toDto(Car car);

    Car toEntity(CreateCarRequestDto requestDto);

    void updateCarFromDto(CreateCarRequestDto dto, @MappingTarget Car entity);
}
