package org.example.springmvc.drivers.dto;

import org.example.springmvc.cars.dto.CarDTO;

import java.util.List;
import java.util.UUID;

public record DriverDTO(
        UUID id,
        String email,
        String fname,
        String lname,
        String ssn,
        List<CarDTO> cars
) {
}
