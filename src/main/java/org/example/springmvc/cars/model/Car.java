package org.example.springmvc.cars.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springmvc.drivers.model.Driver;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Year;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String make;
    @NotBlank
    private String model;

    @NotNull
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal hourlyPrice;

    @NotBlank
    @Column(unique = true)
    @Size(min = 6, max = 6)
    private String licencePlate;

    @NotBlank
    @Column(unique = true)
    private String vin;

    @NotNull
    @PastOrPresent
    private Year year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = true)
    private Driver driver;

    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;

    public Car(String make, String model, BigDecimal hourlyPrice, String licencePlate, String vin, Year year) {
        this.make = make;
        this.model = model;
        this.hourlyPrice = hourlyPrice;
        this.licencePlate = licencePlate;
        this.vin = vin;
        this.year = year;
    }
}
