package org.example.springmvc.cars;

import java.time.Year;

public record CarFilter(
        String q,
        String make,
        String model,
        Year year,
        String licencePlate,
        String vin
) {}