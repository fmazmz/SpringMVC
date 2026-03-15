package org.example.springmvc.bookings;

import org.example.springmvc.insurances.InsuranceType;

import java.util.UUID;

public record BookingFilter(
        String q,
        UUID carId,
        UUID driverId,
        InsuranceType insuranceType
) {}