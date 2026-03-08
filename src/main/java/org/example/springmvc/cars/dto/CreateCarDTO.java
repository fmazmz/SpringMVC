package org.example.springmvc.cars.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Year;

public record CreateCarDTO(
        @NotBlank
        String make,

        @NotBlank
        String model,

        @NotNull
        @DecimalMin(value = "0", inclusive = true)
        BigDecimal hourlyPrice,

        @NotBlank
        @Size(min = 6, max = 6)
        String licencePlate,

        @NotBlank
        String vin,

        @NotNull
        @PastOrPresent
        Year year
) {
}
