package org.example.springmvc.bookings;

import org.example.springmvc.bookings.dto.BookingDTO;
import org.example.springmvc.bookings.dto.CreateBookingDTO;
import org.example.springmvc.bookings.dto.UpdateBookingDTO;
import org.example.springmvc.bookings.model.Booking;
import org.example.springmvc.cars.model.Car;
import org.example.springmvc.drivers.model.Driver;

import java.math.BigDecimal;


public class BookingMapper {

    public static BookingDTO toDto(Booking booking) {
        return new BookingDTO(
                booking.getDriver().getId(),
                booking.getCar().getId(),
                booking.getCar().getMake(),
                booking.getCar().getModel(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getInsuranceType(),
                booking.getTotalPrice());
    }

    public static Booking fromDto(CreateBookingDTO dto, Car car, Driver driver, BigDecimal totalPrice) {
        return new Booking(
                car,
                driver,
                dto.startTime(),
                dto.endTime(),
                dto.insuranceType(),
                totalPrice
        );
    }

    public static void updateEntity(
            Booking booking,
            Car car,
            Driver driver,
            UpdateBookingDTO dto,
            BigDecimal totalPrice
    ) {

        booking.setCar(car);
        booking.setDriver(driver);
        booking.setStartTime(dto.startTime());
        booking.setEndTime(dto.endTime());
        booking.setInsuranceType(dto.insuranceType());
        booking.setTotalPrice(totalPrice);
    }
}