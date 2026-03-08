package org.example.springmvc.cars.dto;

import java.math.BigDecimal;
import java.time.Year;
import java.util.UUID;

public record CarDTO(
        UUID id,
        String make,
        String model,
        BigDecimal hourlyPrice,
        String licencePlate,
        String vin,
        Year year
) {
}
