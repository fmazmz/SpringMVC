package org.example.springmvc.bookings.model;

import org.example.springmvc.insurances.InsuranceType;

import java.util.UUID;

public record BookingFilter(

        UUID carId,
        UUID driverId,
        InsuranceType insuranceType
) {}