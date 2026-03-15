package org.example.springmvc.cars.dto;

import java.math.BigDecimal;
import java.time.Year;

public record CarFilter(
        String q,
        String make,
        String model,
        Year year,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String licencePlate,
        String vin
) {}