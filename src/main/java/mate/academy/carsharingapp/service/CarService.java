package mate.academy.carsharingapp.service;

import mate.academy.carsharingapp.dao.car.CarDto;
import mate.academy.carsharingapp.dao.car.CreateCarRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarDto save(CreateCarRequestDto requestDto);

    Page<CarDto> findAll(Pageable pageable);

    CarDto getById(Long id);

    CarDto update(Long id, CreateCarRequestDto carDto);

    void delete(Long id);
}
