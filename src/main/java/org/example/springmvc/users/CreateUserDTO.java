package org.example.springmvc.users;

public record CreateUserDTO(
        String email,
        String password
) {
}
