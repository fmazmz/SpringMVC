package org.example.springmvc.users;

import org.example.springmvc.exceptions.DuplicateEntityException;
import org.example.springmvc.exceptions.ErrorMessages;
import org.example.springmvc.users.dto.CreateUserDTO;
import org.example.springmvc.users.model.User;
import org.example.springmvc.users.model.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public void create(CreateUserDTO dto) {
        log.debug("Creating user with email={}", dto.email());

        if (repository.existsByEmail(dto.email())) {
            throw new DuplicateEntityException(ErrorMessages.DUPLICATE_USER_EMAIL);
        }

        String encryptedPassword = passwordEncoder.encode(dto.password());

        User user = UserMapper.fromDto(dto, UserRole.APP_USER, encryptedPassword);
        repository.save(user);
        log.info("User created successfully: email={}", dto.email());
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(ErrorMessages.USER_NOT_FOUND_EMAIL, email)
                ));
    }
}

