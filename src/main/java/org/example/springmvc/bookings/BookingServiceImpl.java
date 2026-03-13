package org.example.springmvc.bookings;

import org.example.springmvc.bookings.dto.BookingDTO;
import org.example.springmvc.bookings.dto.CreateBookingDTO;
import org.example.springmvc.bookings.dto.UpdateBookingDTO;
import org.example.springmvc.bookings.model.Booking;
import org.example.springmvc.cars.CarRepository;
import org.example.springmvc.cars.model.Car;
import org.example.springmvc.drivers.DriverRepository;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.exceptions.*;
import org.example.springmvc.insurances.CarInsurance;
import org.example.springmvc.insurances.InsuranceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
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
        UUID carId = parseUuid(filter.carId());
        UUID driverId = parseUuid(filter.driverId());
        InsuranceType insuranceType = parseInsuranceType(filter.insuranceType());

        if (filter.carId() != null && !filter.carId().isBlank() && carId == null) {
            return Page.empty(pageable);
        }

        if (filter.driverId() != null && !filter.driverId().isBlank() && driverId == null) {
            return Page.empty(pageable);
        }

        if (filter.insuranceType() != null && !filter.insuranceType().isBlank() && insuranceType == null) {
            return Page.empty(pageable);
        }

        return repository.searchBookings(
                wildcard(filter.q()),
                carId,
                driverId,
                insuranceType,
                pageable
        ).map(BookingMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDTO getById(UUID id) {
        return BookingMapper.toDto(findBookingById(id));
    }

    @Override
    public void create(CreateBookingDTO dto) {
        Car car = findCarById(dto.carId());
        Driver driver = findDriverById(dto.driverId());

        validateBookingTime(dto.startTime(), dto.endTime());

        if (repository.existsOverlappingBooking(
                car.getId(),
                dto.startTime(),
                dto.endTime()
        )) {
            throw new DuplicateEntityException(ErrorMessages.BOOKING_DUPLICATE);
        }

        BigDecimal totalPrice = calculateTotalPrice(
                car,
                dto.startTime(),
                dto.endTime(),
                dto.insuranceType()
        );

        Booking booking = BookingMapper.fromDto(dto, car, driver, totalPrice);
        repository.save(booking);
    }

    @Override
    public void update(UUID id, UpdateBookingDTO dto) {
        Booking booking = findBookingById(id);
        Car car = findCarById(dto.carId());
        Driver driver = findDriverById(dto.driverId());

        validateBookingTime(dto.startTime(), dto.endTime());

        boolean bookingWindowChanged =
                !booking.getStartTime().equals(dto.startTime())
                        || !booking.getEndTime().equals(dto.endTime())
                        || !booking.getCar().getId().equals(dto.carId());

        if (bookingWindowChanged && repository.existsOverlappingBookingExcludingId(
                car.getId(),
                dto.startTime(),
                dto.endTime(),
                id
        )) {
            throw new DuplicateEntityException(ErrorMessages.BOOKING_DUPLICATE);
        }

        BigDecimal totalPrice = calculateTotalPrice(
                car,
                dto.startTime(),
                dto.endTime(),
                dto.insuranceType()
        );

        BookingMapper.updateEntity(booking, car, driver, dto, totalPrice);
    }

    @Override
    public void delete(UUID id) {
        Booking booking = findBookingById(id);
        repository.delete(booking);
    }

    @Override
    public void deleteByDriver(UUID bookingId, UUID driverId) {
        Booking booking = findBookingById(bookingId);

        if (!booking.getDriver().getId().equals(driverId)) {
            throw new UnauthorizedActionException(ErrorMessages.UNAUTHORIZED_BOOKING_ACTION);
        }

        repository.delete(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingDTO> getDriverBookings(UUID driverId, Pageable pageable) {
        return repository.findByDriverId(driverId, pageable)
                .map(BookingMapper::toDto);
    }

    private String wildcard(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return "%" + value.trim().toLowerCase() + "%";
    }

    private UUID parseUuid(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private InsuranceType parseInsuranceType(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return InsuranceType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Booking findBookingById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.BOOKING_NOT_FOUND_ID, id)
                ));
    }

    private Car findCarById(UUID id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.CAR_NOT_FOUND_ID, id)
                ));
    }

    private Driver findDriverById(UUID id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ErrorMessages.DRIVER_NOT_FOUND_ID, id)
                ));
    }

    private void validateBookingTime(Instant startTime, Instant endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new InvalidBookingTimeException(ErrorMessages.INVALID_BOOKING_TIME);
        }

        Duration duration = Duration.between(startTime, endTime);

        if (duration.toMinutes() < 60) {
            throw new InvalidBookingTimeException("Booking duration must be at least 1 hour");
        }
    }

    private BigDecimal calculateTotalPrice(
            Car car,
            Instant startTime,
            Instant endTime,
            InsuranceType insuranceType
    ) {
        long hours = Duration.between(startTime, endTime).toHours();
        BigDecimal carCost = car.getHourlyPrice().multiply(BigDecimal.valueOf(hours));
        BigDecimal insuranceCost = insurance.getPrice(insuranceType);

        return carCost.add(insuranceCost);
    }
}