package org.example.springmvc.bookings;

import org.example.springmvc.bookings.dto.BookingDTO;
import org.example.springmvc.bookings.dto.CreateBookingDTO;
import org.example.springmvc.bookings.model.Booking;
import org.example.springmvc.cars.CarRepository;
import org.example.springmvc.cars.model.Car;
import org.example.springmvc.drivers.DriverRepository;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.exceptions.DuplicateEntityException;
import org.example.springmvc.exceptions.EntityNotFoundException;
import org.example.springmvc.exceptions.InvalidBookingTimeException;
import org.example.springmvc.exceptions.UnauthorizedActionException;
import org.example.springmvc.insurances.CarInsurance;
import org.example.springmvc.insurances.InsuranceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository repository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private CarInsurance insurance;

    @InjectMocks
    private BookingService bookingService;

    private Car createSampleCar() {
        Car car = new Car("Volvo", "XC90", new BigDecimal("100.00"), "ABC123", "VIN123456", Year.of(2022));
        car.setId(UUID.randomUUID());
        return car;
    }

    private Driver createSampleDriver() {
        Driver driver = new Driver("Erik", "Svensson", "1990010112");
        driver.setId(UUID.randomUUID());
        return driver;
    }

    private Booking createSampleBooking(Car car, Driver driver) {
        Instant start = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant end = start.plus(3, ChronoUnit.HOURS);
        Booking booking = new Booking(car, driver, start, end, InsuranceType.BASIC, new BigDecimal("379.00"));
        booking.setId(UUID.randomUUID());
        return booking;
    }

    @Test
    void getById_shouldReturnBookingDTO_whenBookingExists() {
        Car car = createSampleCar();
        Driver driver = createSampleDriver();
        Booking booking = createSampleBooking(car, driver);

        when(repository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDTO result = bookingService.getById(booking.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.id());
        assertEquals(car.getId(), result.carId());
        assertEquals(driver.getId(), result.driverId());
    }

    @Test
    void getById_shouldThrowEntityNotFoundException_whenBookingMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getById(id));
    }

    @Test
    void create_shouldSucceed_whenValid() {
        Car car = createSampleCar();
        Driver driver = createSampleDriver();
        Instant start = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant end = start.plus(3, ChronoUnit.HOURS);

        CreateBookingDTO dto = new CreateBookingDTO(car.getId(), driver.getId(), start, end, InsuranceType.BASIC);

        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
        when(repository.existsOverlappingBooking(car.getId(), start, end)).thenReturn(false);
        when(insurance.getPrice(InsuranceType.BASIC)).thenReturn(new BigDecimal("79.0"));

        bookingService.create(dto);

        verify(repository).save(any(Booking.class));
    }

    @Test
    void create_shouldThrowDuplicateEntityException_whenOverlapping() {
        Car car = createSampleCar();
        Driver driver = createSampleDriver();
        Instant start = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant end = start.plus(3, ChronoUnit.HOURS);

        CreateBookingDTO dto = new CreateBookingDTO(car.getId(), driver.getId(), start, end, InsuranceType.BASIC);

        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
        when(repository.existsOverlappingBooking(car.getId(), start, end)).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> bookingService.create(dto));
        verify(repository, never()).save(any());
    }

    @Test
    void create_shouldThrowInvalidBookingTimeException_whenStartAfterEnd() {
        Car car = createSampleCar();
        Driver driver = createSampleDriver();
        Instant start = Instant.now().plus(2, ChronoUnit.DAYS);
        Instant end = start.minus(1, ChronoUnit.HOURS);

        CreateBookingDTO dto = new CreateBookingDTO(car.getId(), driver.getId(), start, end, InsuranceType.BASIC);

        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));

        assertThrows(InvalidBookingTimeException.class, () -> bookingService.create(dto));
        verify(repository, never()).save(any());
    }

    @Test
    void create_shouldThrowInvalidBookingTimeException_whenStartInPast() {
        Car car = createSampleCar();
        Driver driver = createSampleDriver();
        Instant start = Instant.now().minus(1, ChronoUnit.HOURS);
        Instant end = Instant.now().plus(2, ChronoUnit.HOURS);

        CreateBookingDTO dto = new CreateBookingDTO(car.getId(), driver.getId(), start, end, InsuranceType.BASIC);

        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));

        assertThrows(InvalidBookingTimeException.class, () -> bookingService.create(dto));
        verify(repository, never()).save(any());
    }

    @Test
    void create_shouldThrowEntityNotFoundException_whenCarMissing() {
        UUID carId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        Instant start = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant end = start.plus(3, ChronoUnit.HOURS);

        CreateBookingDTO dto = new CreateBookingDTO(carId, driverId, start, end, InsuranceType.BASIC);

        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.create(dto));
        verify(repository, never()).save(any());
    }

    @Test
    void delete_shouldSucceed_whenBookingExists() {
        Car car = createSampleCar();
        Driver driver = createSampleDriver();
        Booking booking = createSampleBooking(car, driver);

        when(repository.findById(booking.getId())).thenReturn(Optional.of(booking));

        bookingService.delete(booking.getId());

        verify(repository).delete(booking);
    }

    @Test
    void delete_shouldThrowEntityNotFoundException_whenBookingMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.delete(id));
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteByDriver_shouldSucceed_whenDriverOwnsBooking() {
        Car car = createSampleCar();
        Driver driver = createSampleDriver();
        Booking booking = createSampleBooking(car, driver);

        when(repository.findById(booking.getId())).thenReturn(Optional.of(booking));

        bookingService.deleteByDriver(booking.getId(), driver.getId());

        verify(repository).delete(booking);
    }

    @Test
    void deleteByDriver_shouldThrowUnauthorizedActionException_whenDriverDoesNotOwnBooking() {
        Car car = createSampleCar();
        Driver driver = createSampleDriver();
        Booking booking = createSampleBooking(car, driver);
        UUID otherDriverId = UUID.randomUUID();

        when(repository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(UnauthorizedActionException.class,
                () -> bookingService.deleteByDriver(booking.getId(), otherDriverId));
        verify(repository, never()).delete(any());
    }
}

