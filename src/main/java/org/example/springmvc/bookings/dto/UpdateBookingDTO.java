package org.example.springmvc.bookings.dto;

import jakarta.validation.constraints.NotNull;
import org.example.springmvc.insurances.InsuranceType;

import java.time.Instant;
import java.util.UUID;

public record UpdateBookingDTO(

        @NotNull UUID carId,
        @NotNull UUID driverId,
        @NotNull Instant startTime,
        @NotNull Instant endTime,
        @NotNull InsuranceType insuranceType
) {}