package org.example.springmvc.utils;

import org.example.springmvc.bookings.BookingService;
import org.example.springmvc.bookings.dto.CreateBookingDTO;
import org.example.springmvc.cars.CarRepository;
import org.example.springmvc.cars.model.Car;
import org.example.springmvc.drivers.DriverService;
import org.example.springmvc.drivers.dto.CreateDriverDTO;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.insurances.InsuranceType;
import org.example.springmvc.users.UserRepository;
import org.example.springmvc.users.UserService;
import org.example.springmvc.users.dto.CreateUserDTO;
import org.example.springmvc.users.model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@Profile("!test")
@Order(1)
public class SeedDatabase implements CommandLineRunner {

    private final UserService userService;
    private final UserRepository userRepository;
    private final DriverService driverService;
    private final BookingService bookingService;
    private final CarRepository carRepository;

    public SeedDatabase(UserService userService, UserRepository userRepository, DriverService driverService, 
                       BookingService bookingService, CarRepository carRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.driverService = driverService;
        this.bookingService = bookingService;
        this.carRepository = carRepository;
    }

    @Override
    public void run(String... args) {
        seedTestDriver();
    }

    private void seedTestDriver() {
        String testEmail = "testdriver@email.com";

        CreateUserDTO createUserDTO = new CreateUserDTO(testEmail, "111");
        userService.create(createUserDTO);
        User user = userRepository.findByEmail(testEmail)
                .orElseThrow(() -> new RuntimeException("Failed to create test driver user"));


        CreateDriverDTO createDriverDTO = new CreateDriverDTO(
                "Test",
                "Driver",
                "1234567890"
        );

        driverService.becomeDriver(user.getId(), createDriverDTO);
        user = userRepository.findByEmail(testEmail)
                .orElseThrow(() -> new RuntimeException("Test driver user not found"));


        Driver driver = user.getDriver();
        if (driver == null) {
            throw new RuntimeException("Failed to create test driver");
        }

        List<Car> cars = carRepository.findAll();
        if (cars.isEmpty()) {
            System.out.println("No cars available for seeding booking");
            return;
        }

        Car car = cars.get(0);

        Instant startTime = Instant.now().plusSeconds(86400);
        Instant endTime = Instant.now().plusSeconds(172800);

        try {
            CreateBookingDTO createBookingDTO = new CreateBookingDTO(
                    car.getId(),
                    driver.getId(),
                    startTime,
                    endTime,
                    InsuranceType.BASIC
            );
            bookingService.create(createBookingDTO);
            System.out.println("Seeded test driver with booking: " + testEmail);
        } catch (Exception e) {
            System.out.println("Booking may already exist for test driver: " + e.getMessage());
        }
    }
}
