package carsharingapp.service;

import carsharingapp.dto.car.CarDto;
import carsharingapp.dto.car.CreateCarRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarDto save(CreateCarRequestDto requestDto);

    Page<CarDto> findAll(Pageable pageable);

    CarDto getById(Long id);

    CarDto update(Long id, CreateCarRequestDto carDto);

    void delete(Long id);
}
