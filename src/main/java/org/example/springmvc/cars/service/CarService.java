package org.example.springmvc.cars.service;

import org.example.springmvc.cars.mapper.CarMapper;
import org.example.springmvc.cars.repository.CarRepository;
import org.example.springmvc.cars.dto.CarDTO;
import org.example.springmvc.cars.dto.CreateCarDTO;
import org.example.springmvc.cars.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CarService {
    private final CarRepository repository;

    public CarService(CarRepository repository) {
        this.repository = repository;
    }

    public Page<CarDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(CarMapper::toDto);
    }

    public Page<CarDTO> getByMake(String make, Pageable pageable) {
        return repository.findByMakeIgnoreCase(make, pageable)
                .map(CarMapper::toDto);
    }

    public void create(CreateCarDTO dto) {
        if (repository.findByLicencePlateIgnoreCase(dto.licencePlate().trim()).isPresent()) {
            throw new IllegalArgumentException("A car with license plate '" + dto.licencePlate() + "' already exists.");
        }

        if (repository.findByVinIgnoreCase(dto.vin().trim()).isPresent()) {
            throw new IllegalArgumentException("A car with VIN '" + dto.vin() + "' already exists.");
        }

        Car car = CarMapper.fromDto(dto);
        repository.save(car);
    }
}
