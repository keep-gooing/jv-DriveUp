package mate.academy.carsharingapp.service;

import java.util.List;
import mate.academy.carsharingapp.dao.rental.RentalRequestDto;
import mate.academy.carsharingapp.dao.rental.RentalResponseDto;
import mate.academy.carsharingapp.dao.rental.RentalUpdateDto;
import org.springframework.security.core.Authentication;

public interface RentalService {
    RentalResponseDto createRental(Authentication authentication, RentalRequestDto requestDto);

    List<RentalResponseDto> getRentalsByUserId(Long userId, Boolean isActive);

    RentalResponseDto getSpecificRentalByUser_Id_And_Rental_Id(Long userId, Long rentalId);

    RentalResponseDto updateRentalReturnDate(Long rentalId, RentalUpdateDto updateDto);
}
