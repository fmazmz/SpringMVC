package org.example.springmvc.drivers;

public record DriverFilter(
        String q,
        String fname,
        String lname,
        String ssn,
        String driverId
) {}