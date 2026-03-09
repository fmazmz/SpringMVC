package org.example.springmvc.users;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void create(CreateUserDTO dto) {
        if (repository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Unable to create an account with this email");
        }

        User user = UserMapper.fromDto(dto, UserRole.APP_USER);
        repository.save(user);
    }
}
