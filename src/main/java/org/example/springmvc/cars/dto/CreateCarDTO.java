package org.example.springmvc.cars.dto;

import java.time.Year;

public record CreateCarDTO(
        String make,
        String model,
        Year year
) {
}
