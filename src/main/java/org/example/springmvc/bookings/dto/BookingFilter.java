package org.example.springmvc.bookings.dto;

import org.example.springmvc.insurances.InsuranceType;

import java.util.UUID;

public record BookingFilter(
        String q,
        UUID carId,
        UUID driverId,
        InsuranceType insuranceType
) {}