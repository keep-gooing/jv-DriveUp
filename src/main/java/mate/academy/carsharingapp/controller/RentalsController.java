package mate.academy.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharingapp.dao.rental.RentalRequestDto;
import mate.academy.carsharingapp.dao.rental.RentalResponseDto;
import mate.academy.carsharingapp.dao.rental.RentalUpdateDto;
import mate.academy.carsharingapp.exception.NotificationException;
import mate.academy.carsharingapp.service.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rentals management", description = "Endpoints for managing rentals")
@RequiredArgsConstructor
@RestController
@RequestMapping("/rentals")
public class RentalsController {
    private final RentalService rentalService;

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Operation(summary = "Add a new rental", description = "Add a new rental")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponseDto addRental(Authentication authentication,
                                       @Valid @RequestBody RentalRequestDto dto)
            throws NotificationException {
        return rentalService.createRental(authentication, dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_MANAGER')")
    @Operation(summary = "Get all statuses and rentals by user id",
            description = "Get all statuses and rentals by user id")
    @GetMapping
    public List<RentalResponseDto> getRentalsByUserId(
            @RequestParam Long userId,
            @RequestParam(required = false) Boolean isActive) {
        return rentalService.getRentalsByUserId(userId, isActive);
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_MANAGER')")
    @Operation(summary = "Get specific rental by id",
            description = "Get specific rental by id")
    @GetMapping("/users/{userId}/rentals/{rentalId}")
    public RentalResponseDto getSpecificRentalByUser_Id_And_Rental_Id(
            @PathVariable Long userId,
            @PathVariable Long rentalId) {
        return rentalService.getSpecificRentalByUser_Id_And_Rental_Id(userId, rentalId);
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_MANAGER')")
    @Operation(summary = "Set actual return date", description = "Set actual return date")
    @PutMapping("/{rentalId}/return")
    public RentalResponseDto updateRentalReturnDate(
            @PathVariable Long rentalId,
            @RequestBody @Valid RentalUpdateDto updateDto) {
        return rentalService.updateRentalReturnDate(rentalId, updateDto);
    }
}
