package org.example.springmvc.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springmvc.drivers.model.Driver;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @OneToOne
    @JoinColumn(name = "driver_id", nullable = true)
    private Driver driver;

    public User(String email, String password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
