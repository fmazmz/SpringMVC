package org.example.springmvc.users;

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
            throw new IllegalArgumentException("Unable to create an account with this email");
        }

        String encryptedPassword = passwordEncoder.encode(dto.password());

        User user = UserMapper.fromDto(dto, UserRole.APP_USER, encryptedPassword);
        repository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return repository.findByEmail(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
