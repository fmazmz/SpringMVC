package org.example.springmvc.bookings;

import org.example.springmvc.bookings.dto.BookingDTO;
import org.example.springmvc.bookings.dto.CreateBookingDTO;
import org.example.springmvc.bookings.dto.UpdateBookingDTO;
import org.example.springmvc.bookings.model.Booking;
import org.example.springmvc.bookings.model.BookingFilter;
import org.example.springmvc.cars.CarRepository;
import org.example.springmvc.cars.model.Car;
import org.example.springmvc.drivers.DriverRepository;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.insurances.CarInsurance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final CarRepository carRepository;
    private final DriverRepository driverRepository;
    private final CarInsurance insurance;

    public BookingServiceImpl(
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

    @Override
    @Transactional(readOnly = true)
    public Page<BookingDTO> search(Pageable pageable, BookingFilter filter) {
        return repository.searchBookings(
                filter.carId(),
                filter.driverId(),
                filter.insuranceType(),
                pageable
        ).map(BookingMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDTO getById(UUID id) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        return BookingMapper.toDto(booking);
    }

    @Override
    public void create(CreateBookingDTO dto) {
        Car car = carRepository.findById(dto.carId())
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

        Driver driver = driverRepository.findById(dto.driverId())
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));

        if (!dto.startTime().isBefore(dto.endTime())) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }

        if (repository.existsOverlappingBooking(car.getId(), dto.startTime(), dto.endTime())) {
            throw new IllegalArgumentException("Car already booked for that time.");
        }

        long hours = Duration.between(dto.startTime(), dto.endTime()).toHours();
        BigDecimal carCost = car.getHourlyPrice().multiply(BigDecimal.valueOf(hours));
        BigDecimal insuranceCost = insurance.getPrice(dto.insuranceType());
        BigDecimal total = carCost.add(insuranceCost);
        Booking booking = BookingMapper.fromDto(dto, car, driver, total);

        repository.save(booking);
    }

    @Override
    public void update(UUID id, UpdateBookingDTO dto) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        Car car = carRepository.findById(dto.carId())
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

        Driver driver = driverRepository.findById(dto.driverId())
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));

        if (!dto.startTime().isBefore(dto.endTime())) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }

        long hours = Duration.between(dto.startTime(), dto.endTime()).toHours();
        BigDecimal carCost = car.getHourlyPrice().multiply(BigDecimal.valueOf(hours));
        BigDecimal insuranceCost = insurance.getPrice(dto.insuranceType());
        BigDecimal total = carCost.add(insuranceCost);
        BookingMapper.updateEntity(booking, car, driver, dto, total);

    }

    @Override
    public void delete(UUID id) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        repository.delete(booking);
    }

    @Override
    public void deleteByDriver(UUID bookingId, UUID driverId) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        
        if (!booking.getDriver().getId().equals(driverId)) {
            throw new IllegalArgumentException("You can only cancel your own bookings");
        }
        
        repository.delete(booking);
    }

    @Override
    public Page<BookingDTO> getDriverBookings(UUID driverId, Pageable pageable) {
        return repository.findByDriverId(driverId, pageable)
                .map(BookingMapper::toDto);
    }
}