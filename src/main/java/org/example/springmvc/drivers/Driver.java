package org.example.springmvc.drivers;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springmvc.cars.model.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    private String fname;

    @NotBlank
    private String lname;

    @NotBlank
    @Column(unique = true)
    @Size(min = 10, max = 12)
    private String ssn;

    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private List<Car> cars = new ArrayList<>();
}
