package carsharingapp.mapper;

import carsharingapp.config.MapperConfig;
import carsharingapp.dto.rental.RentalRequestDto;
import carsharingapp.dto.rental.RentalResponseDto;
import carsharingapp.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(target = "carId", source = "car.id")
    @Mapping(target = "userId", source = "user.id")
    RentalResponseDto toDto(Rental rental);

    @Mapping(target = "car.id", source = "carId")
    Rental toEntity(RentalRequestDto requestDto);
}
