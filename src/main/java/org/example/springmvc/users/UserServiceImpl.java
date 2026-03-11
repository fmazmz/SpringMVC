package org.example.springmvc.users;

import org.example.springmvc.exceptions.DuplicateEntityException;
import org.example.springmvc.exceptions.ErrorMessages;
import org.example.springmvc.users.dto.CreateUserDTO;
import org.example.springmvc.users.model.User;
import org.example.springmvc.users.model.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void create(CreateUserDTO dto) {
        if (repository.existsByEmail(dto.email())) {
            throw new DuplicateEntityException(ErrorMessages.DUPLICATE_USER_EMAIL);
        }

        String encryptedPassword = passwordEncoder.encode(dto.password());

        User user = UserMapper.fromDto(dto, UserRole.APP_USER, encryptedPassword);
        repository.save(user);
    }

    @Override
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