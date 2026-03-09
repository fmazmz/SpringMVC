package org.example.springmvc.utils;

import org.example.springmvc.bookings.BookingService;
import org.example.springmvc.bookings.dto.CreateBookingDTO;
import org.example.springmvc.drivers.DriverRepository;
import org.example.springmvc.drivers.DriverService;
import org.example.springmvc.drivers.dto.CreateDriverDTO;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.insurances.InsuranceType;
import org.example.springmvc.users.UserRepository;
import org.example.springmvc.users.UserService;
import org.example.springmvc.users.dto.CreateUserDTO;
import org.example.springmvc.users.model.User;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class DatabaseSeeder {
    private final UserService userService;
    private final UserRepository userRepository;
    private final DriverService driverService;
    private final BookingService bookingService;
    private final DriverRepository driverRepository;

    public DatabaseSeeder(UserService userService, UserRepository userRepository, DriverService driverService, BookingService bookingService, DriverRepository driverRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.driverService = driverService;
        this.bookingService = bookingService;
        this.driverRepository = driverRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedDatabase() {
        userService.create(new CreateUserDTO(
                "john@email.com",
                "driver123"
        ));

        Optional<User> user = userRepository.findByEmail("john@email.com");

        driverService.becomeDriver(user.get().getId(), new CreateDriverDTO(
                "John",
                "Smith",
                "1111111111"
        ));

        Optional<Driver> driver = driverRepository.findBySsn("1111111111");
        bookingService.create(new CreateBookingDTO(
                UUID.fromString("9b2e9b33-39ce-4f33-be9f-22e8c5a780a3"),
                driver.get().getId(),
                Instant.now(),
                Instant.now().plusMillis(100000000),
                InsuranceType.PREMIUM
        ));
    }
}
