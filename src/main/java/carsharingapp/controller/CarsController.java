package carsharingapp.controller;

import carsharingapp.dto.car.CarDto;
import carsharingapp.dto.car.CreateCarRequestDto;
import carsharingapp.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car management", description = "Endpoints for managing cars")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cars")
public class CarsController {
    private final CarService carService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Create a new car", description = "Create a new car")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarDto createCar(@RequestBody @Valid CreateCarRequestDto carDto) {
        return carService.save(carDto);
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Operation(summary = "Get all cars",
            description = "Get paginated and sorted list of all available cars")
    @GetMapping
    public Page<CarDto> getAll(Pageable pageable) {
        return carService.findAll(pageable);
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @Operation(summary = "Get car's detailed information",
            description = "Get car's detailed information")
    @GetMapping("/{id}")
    public CarDto getCarById(@PathVariable Long id) {
        return carService.getById(id);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Update specific car", description = "Update specific car")
    @PutMapping("/{id}")
    public CarDto update(@PathVariable Long id, @RequestBody @Valid CreateCarRequestDto carDto) {
        return carService.update(id, carDto);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Update specific characteristic of the car",
            description = "Update specific characteristic of the car")
    @PatchMapping("/{id}")
    public CarDto updatePartly(@PathVariable Long id,
                               @RequestBody @Valid CreateCarRequestDto carDto) {
        return carService.update(id, carDto);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Delete specific car", description = "Delete specific car")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        carService.delete(id);
    }
}
