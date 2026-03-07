package org.example.springmvc.domain.booking;

import org.example.springmvc.domain.booking.dto.CreateBookingDTO;
import org.example.springmvc.domain.cars.CarRepository;
import org.example.springmvc.domain.cars.model.Car;
import org.example.springmvc.domain.drivers.DriverRepository;
import org.example.springmvc.domain.drivers.model.Driver;
import org.example.springmvc.domain.insurances.CarInsurance;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;

@Service
public class BookingService {
    private final BookingRepository repository;
    private final CarRepository carRepository;
    private final DriverRepository driverRepository;
    private final CarInsurance insurance;

    public BookingService(
            BookingRepository repository,
            CarRepository carRepository,
            DriverRepository driverRepository,
            CarInsurance insurance
    ) {
        this.repository = repository;
        this.carRepository = carRepository;
        this.driverRepository = driverRepository;
        this.insurance = insurance;
    }

    public void create(CreateBookingDTO dto) {
        Car car = carRepository.findById(dto.carId()).orElseThrow();
        Driver driver = driverRepository.findById(dto.driverId()).orElseThrow();

        long hours = Duration.between(dto.startTime(), dto.endTime()).toHours();

        BigDecimal carCost = car.getHourlyPrice()
                .multiply(BigDecimal.valueOf(hours));

        BigDecimal insuranceCost = insurance.getPrice(dto.insuranceType());
        BigDecimal total = carCost.add(insuranceCost);

        Booking booking = new Booking();
        booking.setCar(car);
        booking.setDriver(driver);
        booking.setStartTime(dto.startTime());
        booking.setEndTime(dto.endTime());
        booking.setInsuranceType(dto.insuranceType());
        booking.setTotalPrice(total);

        repository.save(booking);
    }
}