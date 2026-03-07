package org.example.springmvc.drivers;

public record CreateDriverDTO(
        String email,
        String fname,
        String lname,
        String ssn
) {
}
