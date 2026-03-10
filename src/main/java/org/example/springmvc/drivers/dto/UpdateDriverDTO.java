package org.example.springmvc.drivers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateDriverDTO(
        @NotBlank String fname,
        @NotBlank String lname,
        @NotBlank @Size(min = 10, max = 12) String ssn
) {}