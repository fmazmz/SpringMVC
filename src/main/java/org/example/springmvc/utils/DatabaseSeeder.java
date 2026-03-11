package org.example.springmvc.utils;

import org.example.springmvc.bookings.BookingServiceImpl;
import org.example.springmvc.bookings.dto.CreateBookingDTO;
import org.example.springmvc.cars.CarRepository;
import org.example.springmvc.cars.model.Car;
import org.example.springmvc.drivers.DriverServiceImpl;
import org.example.springmvc.drivers.dto.CreateDriverDTO;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.insurances.InsuranceType;
import org.example.springmvc.users.UserRepository;
import org.example.springmvc.users.UserServiceImpl;
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
public class DatabaseSeeder implements CommandLineRunner {

    private final UserServiceImpl userService;
    private final UserRepository userRepository;
    private final DriverServiceImpl driverService;
    private final BookingServiceImpl bookingService;
    private final CarRepository carRepository;

    public DatabaseSeeder(UserServiceImpl userService, UserRepository userRepository, DriverServiceImpl driverService,
                          BookingServiceImpl bookingService, CarRepository carRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.driverService = driverService;
        this.bookingService = bookingService;
        this.carRepository = carRepository;
    }

    @Override
    public void run(String... args) {
        seedTestDriverAndBooking();
    }

    private void seedTestDriverAndBooking() {
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

        List<Car> carsForBooking = List.of(cars.get(0), cars.get(1));

        Instant startTime = Instant.now().plusSeconds(86400);
        Instant endTime = Instant.now().plusSeconds(172800);

        for (Car c : carsForBooking) {
            try {
                CreateBookingDTO createBookingDTO = new CreateBookingDTO(
                        c.getId(),
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
}
