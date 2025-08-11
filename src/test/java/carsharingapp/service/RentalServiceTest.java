package carsharingapp.service;

import static carsharingapp.util.TestUtil.createCar;
import static carsharingapp.util.TestUtil.createRental;
import static carsharingapp.util.TestUtil.createRentalResponseDto;
import static carsharingapp.util.TestUtil.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import carsharingapp.dto.rental.RentalRequestDto;
import carsharingapp.dto.rental.RentalResponseDto;
import carsharingapp.dto.rental.RentalUpdateDto;
import carsharingapp.exception.NotificationException;
import carsharingapp.mapper.RentalMapper;
import carsharingapp.model.Car;
import carsharingapp.model.Rental;
import carsharingapp.model.User;
import carsharingapp.notification.NotificationService;
import carsharingapp.repository.CarRepository;
import carsharingapp.repository.RentalRepository;
import carsharingapp.repository.UserRepository;
import carsharingapp.service.impl.RentalServiceImpl;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private RentalServiceImpl rentalService;

    @Test
    @DisplayName("Create rental - success")
    void createRental_AvailableCar_Success() throws NotificationException {
        Long carId = 1L;
        Long userId = 1L;
        LocalDate rentalDate = LocalDate.now();
        LocalDate returnDate = LocalDate.now().plusWeeks(1);

        RentalRequestDto requestDto = new RentalRequestDto()
                .setCarId(carId)
                .setRentalDate(rentalDate)
                .setReturnDate(returnDate);

        Car availableCar = createCar(carId, 5);
        User user = createUser(userId);
        Rental savedRental = createRental(1L, carId, userId, rentalDate, returnDate, null);

        RentalResponseDto expectedDto = createRentalResponseDto(savedRental);
        String email = "user@example.com";
        when(authentication.getName()).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(carRepository.findById(carId)).thenReturn(Optional.of(availableCar));
        lenient().when(rentalMapper.toEntity(any(RentalRequestDto.class)))
                .thenAnswer(invocation -> {
                    RentalRequestDto argDto = invocation.getArgument(0);
                    return createRental(null, argDto.getCarId(), userId,
                            argDto.getRentalDate(), argDto.getReturnDate(), null);
                });
        when(rentalRepository.save(any(Rental.class))).thenReturn(savedRental);
        when(carRepository.save(any(Car.class))).thenReturn(availableCar);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(expectedDto);
        doNothing().when(notificationService).sendRentalCreationNotification(any(Rental.class));

        RentalResponseDto actualDto = rentalService.createRental(authentication, requestDto);

        assertEquals(expectedDto, actualDto);
        assertEquals(4, availableCar.getInventory());
        verify(notificationService).sendRentalCreationNotification(savedRental);
    }

    @Test
    @DisplayName("Create rental - throws IllegalStateException for unavailable car")
    void createRental_UnavailableCar_ThrowsIllegalStateException() {
        Long carId = 1L;
        RentalRequestDto requestDto = new RentalRequestDto()
                .setCarId(carId)
                .setRentalDate(LocalDate.now())
                .setReturnDate(LocalDate.now().plusWeeks(1));
        Car unavailableCar = createCar(carId, 0);

        when(carRepository.findById(carId)).thenReturn(Optional.of(unavailableCar));

        Exception exception = assertThrows(IllegalStateException.class,
                () -> rentalService.createRental(authentication, requestDto));
        assertEquals("Car with id " + carId + " is not available", exception.getMessage());
    }

    @Test
    @DisplayName("Get rentals by user ID - returns filtered list")
    void getRentalsByUserId_ValidArgs_ReturnsFilteredList() {
        Long userId = 1L;
        LocalDate rentalDate = LocalDate.now().minusDays(5);
        LocalDate returnDate = LocalDate.now().plusDays(5);
        LocalDate actualReturnDate = LocalDate.now().minusDays(2);

        Rental activeRental = createRental(1L, 1L, userId,
                rentalDate, returnDate, null);
        Rental inactiveRental = createRental(2L, 2L, userId,
                rentalDate.minusDays(10), returnDate.minusDays(3), actualReturnDate);
        List<Rental> allRentals = List.of(activeRental, inactiveRental);

        RentalResponseDto activeDto = createRentalResponseDto(activeRental);
        RentalResponseDto inactiveDto = createRentalResponseDto(inactiveRental);

        when(rentalRepository.getRentalsByUser_Id(userId)).thenReturn(allRentals);
        when(rentalMapper.toDto(activeRental)).thenReturn(activeDto);
        when(rentalMapper.toDto(inactiveRental)).thenReturn(inactiveDto);

        List<RentalResponseDto> actualActive = rentalService.getRentalsByUserId(userId, true);
        assertEquals(1, actualActive.size());
        assertEquals(activeDto, actualActive.get(0));

        List<RentalResponseDto> actualInactive = rentalService.getRentalsByUserId(userId, false);
        assertEquals(1, actualInactive.size());
        assertEquals(inactiveDto, actualInactive.get(0));

        verify(rentalRepository, times(2))
                .getRentalsByUser_Id(userId);
    }

    @Test
    @DisplayName("Get specific rental by user and rental ID - success")
    void getSpecificRental_ValidIds_Success() {
        Long userId = 1L;
        Long rentalId = 1L;
        Rental rental = createRental(rentalId, 1L, userId, LocalDate.now(),
                LocalDate.now().plusDays(5), null);
        RentalResponseDto expectedDto = createRentalResponseDto(rental);

        when(rentalRepository.findByUser_IdAndId(userId, rentalId)).thenReturn(Optional.of(rental));
        when(rentalMapper.toDto(rental)).thenReturn(expectedDto);

        RentalResponseDto actualDto = rentalService
                .getSpecificRentalByUser_Id_And_Rental_Id(userId, rentalId);

        assertEquals(expectedDto, actualDto);
        verify(rentalRepository).findByUser_IdAndId(userId, rentalId);
    }

    @Test
    @DisplayName("Update rental return date - success")
    void updateRentalReturnDate_ValidIdAndDto_Success() {
        Long rentalId = 1L;
        LocalDate newActualReturnDate = LocalDate.now();

        Car carInRental = createCar(1L, 0);
        Rental existingRental = createRental(rentalId, 1L, 1L,
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(2), null);
        existingRental.setCar(carInRental);

        Rental updatedRental = createRental(rentalId, 1L, 1L,
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(2),
                newActualReturnDate);
        updatedRental.setCar(carInRental);

        RentalResponseDto expectedDto = createRentalResponseDto(updatedRental);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(existingRental));
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(expectedDto);
        doNothing().when(notificationService).sendRentalReturnNotification(any(Rental.class));

        RentalUpdateDto updateDto = new RentalUpdateDto()
                .setReturnDate(newActualReturnDate);

        RentalResponseDto actualDto = rentalService.updateRentalReturnDate(rentalId, updateDto);

        assertEquals(expectedDto, actualDto);
        assertEquals(newActualReturnDate, actualDto.getActualReturnDate());
        assertEquals(1, updatedRental.getCar().getInventory());

        verify(notificationService).sendRentalReturnNotification(any(Rental.class));
    }
}
