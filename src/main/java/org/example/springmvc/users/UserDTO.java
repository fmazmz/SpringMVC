package org.example.springmvc.users;

import java.util.UUID;

public record UserDTO(
        UUID id,
        String email,
        UserRole role
) {
}
