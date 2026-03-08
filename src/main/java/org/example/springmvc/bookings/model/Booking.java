package org.example.springmvc.bookings.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springmvc.cars.model.Car;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.insurances.InsuranceType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Car car;

    @ManyToOne
    private Driver driver;

    private Instant startTime;
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    private InsuranceType insuranceType;

    private BigDecimal totalPrice;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public Booking(Car car, Driver driver, Instant startTime, Instant endTime, InsuranceType insuranceType, BigDecimal totalPrice) {
        this.car = car;
        this.driver = driver;
        this.startTime = startTime;
        this.endTime = endTime;
        this.insuranceType = insuranceType;
        this.totalPrice = totalPrice;
    }
}