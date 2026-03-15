package org.example.springmvc.drivers;

import java.util.UUID;

public record DriverFilter(
        String q,
        String fname,
        String lname,
        String ssn,
        UUID driverId
) {}