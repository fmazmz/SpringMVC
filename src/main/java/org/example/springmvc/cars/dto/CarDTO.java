package org.example.springmvc.cars.dto;

import java.time.Year;

public record CarDTO(
        String make,
        String model,
        Year year
) {
}
