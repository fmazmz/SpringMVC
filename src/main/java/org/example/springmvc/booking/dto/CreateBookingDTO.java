package org.example.springmvc.booking.dto;

import org.example.springmvc.insurances.InsuranceType;

import java.time.Instant;
import java.util.UUID;

public record CreateBookingDTO(
        UUID carId,
        UUID driverId,
        Instant startTime,
        Instant endTime,
        InsuranceType insuranceType
) {}