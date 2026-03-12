package org.example.springmvc.drivers.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springmvc.bookings.model.Booking;
import org.example.springmvc.cars.model.Car;
import org.example.springmvc.users.model.User;

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
    private String fname;

    @NotBlank
    private String lname;

    @NotBlank
    @Column(unique = true)
    @Size(min = 10, max = 12)
    private String ssn;

    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    private List<Car> cars = new ArrayList<>();

    @OneToOne(mappedBy = "driver", fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    public Driver(String fname, String lname, String ssn) {
        this.fname = fname;
        this.lname = lname;
        this.ssn = ssn;
    }
}
