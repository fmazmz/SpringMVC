package org.example.springmvc.bookings.dto;

import org.example.springmvc.insurances.InsuranceType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BookingDTO(
        UUID id,
        UUID driverId,
        UUID carId,
        String carMake,
        String carModel,
        Instant startTime,
        Instant endTime,
        InsuranceType insuranceType,
        BigDecimal totalPrice
) {
}
