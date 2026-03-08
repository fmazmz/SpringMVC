package org.example.springmvc.drivers.dto;

public record CreateDriverDTO(
        String email,
        String fname,
        String lname,
        String ssn
) {
}
