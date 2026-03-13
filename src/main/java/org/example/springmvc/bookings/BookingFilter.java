package org.example.springmvc.bookings;

public record BookingFilter(

        String q,
        String carId,
        String driverId,
        String insuranceType
) {}