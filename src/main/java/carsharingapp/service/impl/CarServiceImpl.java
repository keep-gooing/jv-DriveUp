package carsharingapp.service.impl;

import carsharingapp.dto.car.CarDto;
import carsharingapp.dto.car.CreateCarRequestDto;
import carsharingapp.exception.EntityNotFoundException;
import carsharingapp.mapper.CarMapper;
import carsharingapp.model.Car;
import carsharingapp.repository.CarRepository;
import carsharingapp.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto save(CreateCarRequestDto requestDto) {
        Car car = carMapper.toEntity(requestDto);
        return carMapper.toDto(carRepository.save(car));
    }

    @Override
    public Page<CarDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable)
                .map(carMapper::toDto);
    }

    @Override
    public CarDto getById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Canʼt find car by id " + id));
        return carMapper.toDto(car);
    }

    @Override
    public CarDto update(Long id, CreateCarRequestDto carDto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find car by id " + id));
        carMapper.updateCarFromDto(carDto, car);
        carRepository.save(car);
        return carMapper.toDto(car);
    }

    @Override
    public void delete(Long id) {
        if (!carRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't find car by id " + id);
        }
        carRepository.deleteById(id);
    }
}
