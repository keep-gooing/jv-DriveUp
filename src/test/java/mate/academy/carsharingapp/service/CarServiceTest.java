package mate.academy.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mate.academy.carsharingapp.dao.car.CarDto;
import mate.academy.carsharingapp.dao.car.CreateCarRequestDto;
import mate.academy.carsharingapp.exception.EntityNotFoundException;
import mate.academy.carsharingapp.mapper.CarMapper;
import mate.academy.carsharingapp.model.Car;
import mate.academy.carsharingapp.repository.CarRepository;
import mate.academy.carsharingapp.service.impl.CarServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @InjectMocks
    private CarServiceImpl carService;

    private Car createCar(Long id, String model, int inventory, BigDecimal dailyFee) {
        Car car = new Car();
        car.setId(id);
        car.setModel(model);
        car.setBrand("Brand");
        car.setType(Car.Type.SEDAN);
        car.setInventory(inventory);
        car.setDailyFee(dailyFee);
        return car;
    }

    private CarDto createCarDto(Long id, String model, int inventory, BigDecimal dailyFee) {
        CarDto carDto = new CarDto();
        carDto.setId(id);
        carDto.setModel(model);
        carDto.setBrand("Brand");
        carDto.setType(Car.Type.SEDAN);
        carDto.setInventory(inventory);
        carDto.setDailyFee(dailyFee);
        return carDto;
    }

    private CreateCarRequestDto createCreateCarRequestDto(String model,
                                                          int inventory, BigDecimal dailyFee) {
        CreateCarRequestDto requestDto = new CreateCarRequestDto();
        requestDto.setModel(model);
        requestDto.setBrand("Brand");
        requestDto.setType(Car.Type.SEDAN);
        requestDto.setInventory(inventory);
        requestDto.setDailyFee(dailyFee);
        return requestDto;
    }

    @Test
    @DisplayName("Save new car - success")
    void save_ValidRequestDto_ReturnsCarDto() {
        CreateCarRequestDto requestDto = createCreateCarRequestDto("New Car",
                10, new BigDecimal("150.00"));
        Car carToSave = createCar(null, "New Car", 10, new BigDecimal("150.00"));
        Car savedCar = createCar(1L, "New Car", 10, new BigDecimal("150.00"));
        CarDto expectedDto = createCarDto(1L, "New Car", 10, new BigDecimal("150.00"));

        when(carMapper.toEntity(requestDto)).thenReturn(carToSave);
        when(carRepository.save(carToSave)).thenReturn(savedCar);
        when(carMapper.toDto(savedCar)).thenReturn(expectedDto);

        CarDto actualDto = carService.save(requestDto);

        assertEquals(expectedDto, actualDto);
        verify(carRepository).save(carToSave);
    }

    @Test
    @DisplayName("Find all cars - returns paged list")
    void findAll_ValidPageable_ReturnsPagedCarDtoList() {
        Car car1 = createCar(1L, "Car1", 5, new BigDecimal("100.00"));
        CarDto carDto1 = createCarDto(1L, "Car1", 5, new BigDecimal("100.00"));
        Page<Car> carPage = new PageImpl<>(List.of(car1));
        Pageable pageable = Pageable.ofSize(10);

        when(carRepository.findAll(pageable)).thenReturn(carPage);
        when(carMapper.toDto(car1)).thenReturn(carDto1);

        Page<CarDto> actualPage = carService.findAll(pageable);

        assertEquals(1, actualPage.getTotalElements());
        assertEquals(carDto1, actualPage.getContent().get(0));
        verify(carRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Get car by valid ID - success")
    void getById_ValidId_ReturnsCarDto() {
        Long carId = 1L;
        Car car = createCar(carId, "ModelX", 5, new BigDecimal("100.00"));
        CarDto expectedDto = createCarDto(carId, "ModelX", 5, new BigDecimal("100.00"));

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(expectedDto);

        CarDto actualDto = carService.getById(carId);

        assertEquals(expectedDto, actualDto);
        verify(carRepository).findById(carId);
    }

    @Test
    @DisplayName("Get car by invalid ID - throws EntityNotFoundException")
    void getById_InvalidId_ThrowsEntityNotFoundException() {
        Long invalidCarId = 99L;
        when(carRepository.findById(invalidCarId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> carService.getById(invalidCarId));
        assertEquals("Canʼt find car by id " + invalidCarId, exception.getMessage());
    }

    @Test
    @DisplayName("Delete car by valid ID - success")
    void delete_ValidId_Success() {
        Long carId = 1L;
        when(carRepository.existsById(carId)).thenReturn(true);
        doNothing().when(carRepository).deleteById(carId);

        carService.delete(carId);

        verify(carRepository).existsById(carId);
        verify(carRepository).deleteById(carId);
    }

    @Test
    @DisplayName("Delete car by invalid ID - throws EntityNotFoundException")
    void delete_InvalidId_ThrowsEntityNotFoundException() {
        Long invalidCarId = 99L;
        when(carRepository.existsById(invalidCarId)).thenReturn(false);

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> carService.delete(invalidCarId));
        assertEquals("Can't find car by id " + invalidCarId, exception.getMessage());
        verify(carRepository).existsById(invalidCarId);
    }
}
