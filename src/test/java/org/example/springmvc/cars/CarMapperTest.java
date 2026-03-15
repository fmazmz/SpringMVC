package org.example.springmvc.cars;

import org.example.springmvc.cars.dto.CarDTO;
import org.example.springmvc.cars.dto.CreateCarDTO;
import org.example.springmvc.cars.dto.UpdateCarDTO;
import org.example.springmvc.cars.model.Car;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Year;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CarMapperTest {

    @Test
    void toDto_shouldMapAllFields() {
        Car car = new Car("Volvo", "XC90", new BigDecimal("150.00"), "ABC123", "VIN123456", Year.of(2022));
        car.setId(UUID.randomUUID());

        CarDTO dto = CarMapper.toDto(car);

        assertEquals(car.getId(), dto.id());
        assertEquals("Volvo", dto.make());
        assertEquals("XC90", dto.model());
        assertEquals(new BigDecimal("150.00"), dto.hourlyPrice());
        assertEquals("ABC123", dto.licencePlate());
        assertEquals("VIN123456", dto.vin());
        assertEquals(Year.of(2022), dto.year());
    }

    @Test
    void fromDto_shouldMapAllFieldsAndTrimStrings() {
        CreateCarDTO dto = new CreateCarDTO(" Volvo ", " XC90 ", new BigDecimal("150.00"), " ABC123 ", " VIN123456 ", Year.of(2022));

        Car car = CarMapper.fromDto(dto);

        assertEquals("Volvo", car.getMake());
        assertEquals("XC90", car.getModel());
        assertEquals(new BigDecimal("150.00"), car.getHourlyPrice());
        assertEquals("ABC123", car.getLicencePlate());
        assertEquals("VIN123456", car.getVin());
        assertEquals(Year.of(2022), car.getYear());
        assertNull(car.getId());
    }

    @Test
    void updateEntity_shouldUpdateAllFieldsAndTrimStrings() {
        Car car = new Car("Volvo", "XC90", new BigDecimal("150.00"), "ABC123", "VIN123456", Year.of(2022));
        UpdateCarDTO dto = new UpdateCarDTO(" BMW ", " X5 ", new BigDecimal("200.00"), " DEF456 ", " VIN999999 ", Year.of(2023));

        CarMapper.updateEntity(car, dto);

        assertEquals("BMW", car.getMake());
        assertEquals("X5", car.getModel());
        assertEquals(new BigDecimal("200.00"), car.getHourlyPrice());
        assertEquals("DEF456", car.getLicencePlate());
        assertEquals("VIN999999", car.getVin());
        assertEquals(Year.of(2023), car.getYear());
    }
}

