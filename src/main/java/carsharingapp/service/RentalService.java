package carsharingapp.service;

import carsharingapp.dto.rental.RentalRequestDto;
import carsharingapp.dto.rental.RentalResponseDto;
import carsharingapp.dto.rental.RentalUpdateDto;
import java.util.List;
import org.springframework.security.core.Authentication;

public interface RentalService {
    RentalResponseDto createRental(Authentication authentication, RentalRequestDto requestDto);

    List<RentalResponseDto> getRentalsByUserId(Long userId, Boolean isActive);

    RentalResponseDto getSpecificRentalByUser_Id_And_Rental_Id(Long userId, Long rentalId);

    RentalResponseDto updateRentalReturnDate(Long rentalId, RentalUpdateDto updateDto);
}
