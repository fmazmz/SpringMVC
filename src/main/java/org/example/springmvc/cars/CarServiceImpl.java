package org.example.springmvc.cars;

import org.example.springmvc.cars.dto.CarDTO;
import org.example.springmvc.cars.dto.CreateCarDTO;
import org.example.springmvc.cars.dto.UpdateCarDTO;
import org.example.springmvc.cars.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
public class CarServiceImpl implements CarService{
    private final CarRepository repository;

    public CarServiceImpl(CarRepository repository) {
        this.repository = repository;
    }

    @Override
    public void create(CreateCarDTO dto) {
        String plate = dto.licencePlate().trim();
        String vin = dto.vin().trim();

        if (repository.findByLicencePlateIgnoreCase(plate).isPresent()) {
            throw new IllegalArgumentException(
                    "A car with license plate '" + plate + "' already exists."
            );
        }

        if (repository.findByVinIgnoreCase(vin).isPresent()) {
            throw new IllegalArgumentException(
                    "A car with VIN '" + vin + "' already exists."
            );
        }
        Car car = CarMapper.fromDto(dto);
        repository.save(car);
    }

    @Override
    public Page<CarDTO> search(Pageable pageable, CarFilter filter) {
        return repository.searchCars(
                wildcard(filter.q()),
                wildcard(filter.make()),
                wildcard(filter.model()),
                filter.year(),
                wildcard(filter.licencePlate()),
                wildcard(filter.vin()),
                pageable
        ).map(CarMapper::toDto);
    }

    @Override
    public List<CarDTO> findAvailable(Instant startTime, Instant endTime) {
        return repository.findAvailableCars(startTime, endTime)
                .stream()
                .map(CarMapper::toDto)
                .toList();
    }

    @Override
    public CarDTO getById(UUID id) {
        Car car = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id " + id));
        return CarMapper.toDto(car);
    }

    @Override
    public void update(UUID id, UpdateCarDTO dto) {
        Car car = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id " + id));

        String plate = dto.licencePlate().trim();
        String vin = dto.vin().trim();

        if (repository.existsByLicencePlateIgnoreCaseAndIdNot(plate, id)) {
            throw new IllegalArgumentException(
                    "Another car with license plate '" + plate + "' already exists."
            );
        }

        if (repository.existsByVinIgnoreCaseAndIdNot(vin, id)) {
            throw new IllegalArgumentException(
                    "Another car with VIN '" + vin + "' already exists."
            );
        }
        CarMapper.updateEntity(car, dto);
        repository.save(car);
    }

    @Override
    public void delete(UUID id) {
        Car car = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id " + id));
        repository.delete(car);
    }

    private String wildcard(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return "%" + value.trim().toLowerCase() + "%";
    }
}