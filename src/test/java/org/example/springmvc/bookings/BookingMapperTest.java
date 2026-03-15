package org.example.springmvc.bookings;

import org.example.springmvc.bookings.dto.BookingDTO;
import org.example.springmvc.bookings.dto.CreateBookingDTO;
import org.example.springmvc.bookings.dto.UpdateBookingDTO;
import org.example.springmvc.bookings.model.Booking;
import org.example.springmvc.cars.model.Car;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.insurances.InsuranceType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private Car createSampleCar() {
        Car car = new Car("Volvo", "XC90", new BigDecimal("150.00"), "ABC123", "VIN123456", Year.of(2022));
        car.setId(UUID.randomUUID());
        return car;
    }

    private Driver createSampleDriver() {
        Driver driver = new Driver("Erik", "Svensson", "1990010112");
        driver.setId(UUID.randomUUID());
        return driver;
    }

    @Test
    void toDto_shouldMapAllFields() {
        Car car = createSampleCar();
        Driver driver = createSampleDriver();
        Instant start = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant end = start.plus(3, ChronoUnit.HOURS);

        Booking booking = new Booking(car, driver, start, end, InsuranceType.PREMIUM, new BigDecimal("529.00"));
        booking.setId(UUID.randomUUID());

        BookingDTO dto = BookingMapper.toDto(booking);

        assertEquals(booking.getId(), dto.id());
        assertEquals(driver.getId(), dto.driverId());
        assertEquals(car.getId(), dto.carId());
        assertEquals("Volvo", dto.carMake());
        assertEquals("XC90", dto.carModel());
        assertEquals(start, dto.startTime());
        assertEquals(end, dto.endTime());
        assertEquals(InsuranceType.PREMIUM, dto.insuranceType());
        assertEquals(new BigDecimal("529.00"), dto.totalPrice());
    }

    @Test
    void fromDto_shouldCreateBookingFromCreateDTO() {
        Car car = createSampleCar();
        Driver driver = createSampleDriver();
        Instant start = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant end = start.plus(3, ChronoUnit.HOURS);
        BigDecimal totalPrice = new BigDecimal("529.00");

        CreateBookingDTO dto = new CreateBookingDTO(car.getId(), driver.getId(), start, end, InsuranceType.BASIC);

        Booking booking = BookingMapper.fromDto(dto, car, driver, totalPrice);

        assertEquals(car, booking.getCar());
        assertEquals(driver, booking.getDriver());
        assertEquals(start, booking.getStartTime());
        assertEquals(end, booking.getEndTime());
        assertEquals(InsuranceType.BASIC, booking.getInsuranceType());
        assertEquals(totalPrice, booking.getTotalPrice());
        assertNull(booking.getId());
    }

    @Test
    void updateEntity_shouldUpdateAllFields() {
        Car originalCar = createSampleCar();
        Driver originalDriver = createSampleDriver();
        Instant originalStart = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant originalEnd = originalStart.plus(2, ChronoUnit.HOURS);

        Booking booking = new Booking(originalCar, originalDriver, originalStart, originalEnd, InsuranceType.BASIC, new BigDecimal("379.00"));

        Car newCar = createSampleCar();
        Driver newDriver = createSampleDriver();
        Instant newStart = Instant.now().plus(2, ChronoUnit.DAYS);
        Instant newEnd = newStart.plus(5, ChronoUnit.HOURS);
        BigDecimal newPrice = new BigDecimal("949.00");

        UpdateBookingDTO dto = new UpdateBookingDTO(newCar.getId(), newDriver.getId(), newStart, newEnd, InsuranceType.FULL_COVERAGE);

        BookingMapper.updateEntity(booking, newCar, newDriver, dto, newPrice);

        assertEquals(newCar, booking.getCar());
        assertEquals(newDriver, booking.getDriver());
        assertEquals(newStart, booking.getStartTime());
        assertEquals(newEnd, booking.getEndTime());
        assertEquals(InsuranceType.FULL_COVERAGE, booking.getInsuranceType());
        assertEquals(newPrice, booking.getTotalPrice());
    }
}

