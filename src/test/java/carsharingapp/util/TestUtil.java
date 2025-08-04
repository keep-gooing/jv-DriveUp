package carsharingapp.util;

import carsharingapp.dto.car.CarDto;
import carsharingapp.dto.car.CreateCarRequestDto;
import carsharingapp.dto.rental.RentalResponseDto;
import carsharingapp.model.Car;
import carsharingapp.model.Rental;
import carsharingapp.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class TestUtil {
    private TestUtil() {
    }

    public static String getFirstBookIsbn() {
        return "0000000001";
    }

    public static CreateCarRequestDto createCarRequestDto(String model, String brand, Car.Type type,
                                                    int inventory, BigDecimal dailyFee) {
        CreateCarRequestDto dto = new CreateCarRequestDto();
        dto.setModel(model);
        dto.setBrand(brand);
        dto.setType(type);
        dto.setInventory(inventory);
        dto.setDailyFee(dailyFee);
        return dto;
    }

    public static Car createCar(Long id, String model, int inventory, BigDecimal dailyFee) {
        Car car = new Car();
        car.setId(id);
        car.setModel(model);
        car.setBrand("Brand");
        car.setType(Car.Type.SEDAN);
        car.setInventory(inventory);
        car.setDailyFee(dailyFee);
        return car;
    }

    public static Car createCar(Long id, int inventory) {
        Car car = new Car();
        car.setId(id);
        car.setInventory(inventory);
        car.setDailyFee(new BigDecimal("100.00"));
        car.setModel("TestModel");
        car.setBrand("TestBrand");
        car.setType(Car.Type.SEDAN);
        return car;
    }

    public static CarDto createCarDto(Long id, String model, int inventory, BigDecimal dailyFee) {
        CarDto carDto = new CarDto();
        carDto.setId(id);
        carDto.setModel(model);
        carDto.setBrand("Toyota");
        carDto.setType(Car.Type.SEDAN);
        carDto.setInventory(inventory);
        carDto.setDailyFee(dailyFee);
        return carDto;
    }

    public static CreateCarRequestDto createCreateCarRequestDto(String model,
                                                          int inventory, BigDecimal dailyFee) {
        CreateCarRequestDto requestDto = new CreateCarRequestDto();
        requestDto.setModel(model);
        requestDto.setBrand("Brand");
        requestDto.setType(Car.Type.SEDAN);
        requestDto.setInventory(inventory);
        requestDto.setDailyFee(dailyFee);
        return requestDto;
    }

    public static User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("user" + id + "@example.com");
        return user;
    }

    public static Rental createRental(Long rentalId, Long carId, Long userId, LocalDate rentalDate,
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

    public static RentalResponseDto createRentalResponseDto(Rental rental) {
        RentalResponseDto dto = new RentalResponseDto();
        dto.setId(rental.getId());
        dto.setCarId(rental.getCar().getId());
        dto.setUserId(rental.getUser().getId());
        dto.setRentalDate(rental.getRentalDate());
        dto.setReturnDate(rental.getReturnDate());
        dto.setActualReturnDate(rental.getActualReturnDate());
        return dto;
    }
}
