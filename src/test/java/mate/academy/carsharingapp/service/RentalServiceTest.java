package mate.academy.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mate.academy.carsharingapp.dao.rental.RentalRequestDto;
import mate.academy.carsharingapp.dao.rental.RentalResponseDto;
import mate.academy.carsharingapp.dao.rental.RentalUpdateDto;
import mate.academy.carsharingapp.exception.NotificationException;
import mate.academy.carsharingapp.mapper.RentalMapper;
import mate.academy.carsharingapp.model.Car;
import mate.academy.carsharingapp.model.Rental;
import mate.academy.carsharingapp.model.User;
import mate.academy.carsharingapp.notification.NotificationService;
import mate.academy.carsharingapp.repository.CarRepository;
import mate.academy.carsharingapp.repository.RentalRepository;
import mate.academy.carsharingapp.repository.UserRepository;
import mate.academy.carsharingapp.service.impl.RentalServiceImpl;
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

    private Car createCar(Long id, int inventory) {
        Car car = new Car();
        car.setId(id);
        car.setInventory(inventory);
        car.setDailyFee(new BigDecimal("100.00"));
        car.setModel("TestModel");
        car.setBrand("TestBrand");
        car.setType(Car.Type.SEDAN);
        return car;
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("user" + id + "@example.com");
        return user;
    }

    private Rental createRental(Long rentalId, Long carId, Long userId, LocalDate rentalDate,
                                LocalDate returnDate, LocalDate actualReturnDate) {
        Rental rental = new Rental();
        rental.setId(rentalId);
        rental.setRentalDate(rentalDate);
        rental.setReturnDate(returnDate);
        rental.setActualReturnDate(actualReturnDate);
        rental.setCar(createCar(carId, 1));
        rental.setUser(createUser(userId));
        return rental;
    }

    private RentalResponseDto createRentalResponseDto(Rental rental) {
        RentalResponseDto dto = new RentalResponseDto();
        dto.setId(rental.getId());
        dto.setCarId(rental.getCar().getId());
        dto.setUserId(rental.getUser().getId());
        dto.setRentalDate(rental.getRentalDate());
        dto.setReturnDate(rental.getReturnDate());
        dto.setActualReturnDate(rental.getActualReturnDate());
        return dto;
    }

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

        Rental updatedRental = createRental(rentalId, 1L, 1L, LocalDate.now()
                .minusDays(5), LocalDate.now().plusDays(2), newActualReturnDate);
        updatedRental.setCar(carInRental);

        RentalResponseDto expectedDto = createRentalResponseDto(updatedRental);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(existingRental));
        when(carRepository.save(carInRental))
                .thenReturn(carInRental);
        when(rentalRepository.save(any(Rental.class))).thenReturn(updatedRental);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(expectedDto);
        doNothing().when(notificationService)
                .sendRentalReturnNotification(any(Rental.class));

        RentalUpdateDto updateDto = new RentalUpdateDto()
                .setReturnDate(newActualReturnDate);
        RentalResponseDto actualDto = rentalService
                .updateRentalReturnDate(rentalId, updateDto);

        assertEquals(expectedDto, actualDto);
        assertEquals(newActualReturnDate,
                existingRental.getActualReturnDate());
        assertEquals(1, updatedRental.getCar().getInventory());
        verify(notificationService).sendRentalReturnNotification(updatedRental);
    }
}
