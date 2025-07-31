package mate.academy.carsharingapp.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.carsharingapp.dao.car.CarDto;
import mate.academy.carsharingapp.dao.car.CreateCarRequestDto;
import mate.academy.carsharingapp.exception.EntityNotFoundException;
import mate.academy.carsharingapp.mapper.CarMapper;
import mate.academy.carsharingapp.model.Car;
import mate.academy.carsharingapp.repository.CarRepository;
import mate.academy.carsharingapp.service.CarService;
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
