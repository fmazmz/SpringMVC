package org.example.springmvc.domain.booking;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.springmvc.domain.cars.model.Car;
import org.example.springmvc.domain.drivers.model.Driver;
import org.example.springmvc.domain.insurances.InsuranceType;

import java.math.BigDecimal;
import java.time.Instant;
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
}