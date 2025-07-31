package mate.academy.carsharingapp.service.impl;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharingapp.dao.rental.RentalRequestDto;
import mate.academy.carsharingapp.dao.rental.RentalResponseDto;
import mate.academy.carsharingapp.dao.rental.RentalUpdateDto;
import mate.academy.carsharingapp.exception.EntityNotFoundException;
import mate.academy.carsharingapp.mapper.RentalMapper;
import mate.academy.carsharingapp.model.Car;
import mate.academy.carsharingapp.model.Rental;
import mate.academy.carsharingapp.model.User;
import mate.academy.carsharingapp.notification.NotificationService;
import mate.academy.carsharingapp.repository.CarRepository;
import mate.academy.carsharingapp.repository.RentalRepository;
import mate.academy.carsharingapp.repository.UserRepository;
import mate.academy.carsharingapp.service.RentalService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final NotificationService notificationService;

    @Override
    public RentalResponseDto createRental(Authentication authentication,
                                          RentalRequestDto requestDto) {
        Car car = carRepository.findById(requestDto.getCarId()).orElseThrow(
                () -> new EntityNotFoundException("Car with id "
                        + requestDto.getCarId() + " not found")
        );
        if (car.getInventory() <= 0) {
            throw new IllegalStateException("Car with id " + car.getId() + " is not available");
        }
        Rental rental = new Rental();
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException("User with email "
                        + authentication.getName() + " not found")
        );
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(LocalDate.now().plusWeeks(1));
        rental.setActualReturnDate(null);
        rental.setUser(user);
        rental.setCar(car);
        car.setInventory(car.getInventory() - 1);
        carRepository.save(car);
        Rental saved = rentalRepository.save(rental);
        notificationService.sendRentalCreationNotification(saved);
        return rentalMapper.toDto(rental);
    }

    public List<RentalResponseDto> getRentalsByUserId(Long userId, Boolean isActive) {
        return rentalRepository.getRentalsByUser_Id(userId).stream()
                .filter(rental -> isActive == null
                        || (isActive && rental.getActualReturnDate() == null)
                        || (!isActive && rental.getActualReturnDate() != null))
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public RentalResponseDto getSpecificRentalByUser_Id_And_Rental_Id(Long userId, Long rentalId) {
        Rental rental = rentalRepository
                .findByUser_IdAndId(userId, rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find rental by "
                        + "user id " + userId + ", rental id " + rentalId));
        return rentalMapper.toDto(rental);
    }

    @Override
    public RentalResponseDto updateRentalReturnDate(Long rentalId, RentalUpdateDto updateDto) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find rental by id "
                        + rentalId));
        if (rental.getActualReturnDate() != null) {
            throw new IllegalStateException("Rental has already been returned");
        }
        rental.setActualReturnDate(updateDto.getReturnDate());
        Car car = rental.getCar();
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
        Rental saved = rentalRepository.save(rental);
        notificationService.sendRentalReturnNotification(saved);
        return rentalMapper.toDto(rental);
    }
}
