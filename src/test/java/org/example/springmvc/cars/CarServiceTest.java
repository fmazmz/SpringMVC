package org.example.springmvc.cars;

import org.example.springmvc.cars.dto.CarDTO;
import org.example.springmvc.cars.dto.CreateCarDTO;
import org.example.springmvc.cars.dto.UpdateCarDTO;
import org.example.springmvc.cars.model.Car;
import org.example.springmvc.exceptions.DuplicateEntityException;
import org.example.springmvc.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository repository;

    @InjectMocks
    private CarService carService;

    private Car createSampleCar() {
        Car car = new Car("Volvo", "XC90", new BigDecimal("150.00"), "ABC123", "VIN123456", Year.of(2022));
        car.setId(UUID.randomUUID());
        return car;
    }

    @Test
    void create_shouldReturnCarDTO_whenNoDuplicates() {
        CreateCarDTO dto = new CreateCarDTO("Volvo", "XC90", new BigDecimal("150.00"), "ABC123", "VIN123456", Year.of(2022));
        Car savedCar = createSampleCar();

        when(repository.findByLicencePlateIgnoreCase("ABC123")).thenReturn(Optional.empty());
        when(repository.findByVinIgnoreCase("VIN123456")).thenReturn(Optional.empty());
        when(repository.save(any(Car.class))).thenReturn(savedCar);

        CarDTO result = carService.create(dto);

        assertNotNull(result);
        assertEquals("Volvo", result.make());
        assertEquals("XC90", result.model());
        verify(repository).save(any(Car.class));
    }

    @Test
    void create_shouldThrowDuplicateEntityException_whenPlateExists() {
        CreateCarDTO dto = new CreateCarDTO("Volvo", "XC90", new BigDecimal("150.00"), "ABC123", "VIN123456", Year.of(2022));

        when(repository.findByLicencePlateIgnoreCase("ABC123")).thenReturn(Optional.of(createSampleCar()));

        assertThrows(DuplicateEntityException.class, () -> carService.create(dto));
        verify(repository, never()).save(any());
    }

    @Test
    void create_shouldThrowDuplicateEntityException_whenVinExists() {
        CreateCarDTO dto = new CreateCarDTO("Volvo", "XC90", new BigDecimal("150.00"), "ABC123", "VIN123456", Year.of(2022));

        when(repository.findByLicencePlateIgnoreCase("ABC123")).thenReturn(Optional.empty());
        when(repository.findByVinIgnoreCase("VIN123456")).thenReturn(Optional.of(createSampleCar()));

        assertThrows(DuplicateEntityException.class, () -> carService.create(dto));
        verify(repository, never()).save(any());
    }

    @Test
    void getById_shouldReturnCarDTO_whenCarExists() {
        Car car = createSampleCar();
        UUID id = car.getId();

        when(repository.findById(id)).thenReturn(Optional.of(car));

        CarDTO result = carService.getById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Volvo", result.make());
    }

    @Test
    void getById_shouldThrowEntityNotFoundException_whenCarMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> carService.getById(id));
    }

    @Test
    void update_shouldSucceed_whenNoDuplicates() {
        Car car = createSampleCar();
        UUID id = car.getId();
        UpdateCarDTO dto = new UpdateCarDTO("BMW", "X5", new BigDecimal("200.00"), "DEF456", "VIN999999", Year.of(2023));

        when(repository.findById(id)).thenReturn(Optional.of(car));
        when(repository.existsByLicencePlateIgnoreCaseAndIdNot("DEF456", id)).thenReturn(false);
        when(repository.existsByVinIgnoreCaseAndIdNot("VIN999999", id)).thenReturn(false);
        when(repository.save(any(Car.class))).thenReturn(car);

        carService.update(id, dto);

        assertEquals("BMW", car.getMake());
        assertEquals("X5", car.getModel());
        verify(repository).save(car);
    }

    @Test
    void update_shouldThrowDuplicateEntityException_whenPlateConflicts() {
        Car car = createSampleCar();
        UUID id = car.getId();
        UpdateCarDTO dto = new UpdateCarDTO("BMW", "X5", new BigDecimal("200.00"), "DEF456", "VIN999999", Year.of(2023));

        when(repository.findById(id)).thenReturn(Optional.of(car));
        when(repository.existsByLicencePlateIgnoreCaseAndIdNot("DEF456", id)).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> carService.update(id, dto));
        verify(repository, never()).save(any());
    }

    @Test
    void update_shouldThrowEntityNotFoundException_whenCarMissing() {
        UUID id = UUID.randomUUID();
        UpdateCarDTO dto = new UpdateCarDTO("BMW", "X5", new BigDecimal("200.00"), "DEF456", "VIN999999", Year.of(2023));

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> carService.update(id, dto));
    }

    @Test
    void delete_shouldSucceed_whenCarExists() {
        Car car = createSampleCar();
        UUID id = car.getId();

        when(repository.findById(id)).thenReturn(Optional.of(car));

        carService.delete(id);

        verify(repository).delete(car);
    }

    @Test
    void delete_shouldThrowEntityNotFoundException_whenCarMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> carService.delete(id));
        verify(repository, never()).delete(any());
    }
}

