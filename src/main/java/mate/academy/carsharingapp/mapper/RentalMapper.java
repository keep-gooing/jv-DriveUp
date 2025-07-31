package mate.academy.carsharingapp.mapper;

import mate.academy.carsharingapp.config.MapperConfig;
import mate.academy.carsharingapp.dao.rental.RentalRequestDto;
import mate.academy.carsharingapp.dao.rental.RentalResponseDto;
import mate.academy.carsharingapp.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(target = "carId", source = "car.id")
    RentalResponseDto toDto(Rental rental);

    @Mapping(target = "car.id", source = "carId")
    Rental toEntity(RentalRequestDto requestDto);
}
