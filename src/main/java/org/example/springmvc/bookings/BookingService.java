package org.example.springmvc.bookings;

import org.example.springmvc.bookings.dto.BookingDTO;
import org.example.springmvc.bookings.dto.BookingFilter;
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
import org.example.springmvc.utils.SearchUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@Transactional
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

    @Transactional(readOnly = true)
    public Page<BookingDTO> search(Pageable pageable, BookingFilter filter) {
        log.debug("Searching bookings with filter: q={}, carId={}, driverId={}", filter.q(), filter.carId(), filter.driverId());
        return repository.searchBookings(
                SearchUtils.toWildcardPattern(filter.q()),
                filter.carId(),
                filter.driverId(),
                filter.insuranceType(),
                pageable
        ).map(BookingMapper::toDto);
    }

    @Transactional(readOnly = true)
    public BookingDTO getById(UUID id) {
        log.debug("Fetching booking by id={}", id);
        return BookingMapper.toDto(findBookingById(id));
    }

    public void create(CreateBookingDTO dto) {
        log.debug("Creating booking: carId={}, driverId={}, startTime={}, endTime={}", dto.carId(), dto.driverId(), dto.startTime(), dto.endTime());

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
        log.info("Booking created successfully: carId={}, driverId={}, totalPrice={}", dto.carId(), dto.driverId(), totalPrice);
    }

    public void update(UUID id, UpdateBookingDTO dto) {
        log.debug("Updating booking: id={}", id);

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
        log.info("Booking updated successfully: id={}", id);
    }

    public void delete(UUID id) {
        log.debug("Deleting booking: id={}", id);
        Booking booking = findBookingById(id);
        repository.delete(booking);
        log.info("Booking deleted successfully: id={}", id);
    }

    public void deleteByDriver(UUID bookingId, UUID driverId) {
        log.debug("Driver deleting booking: bookingId={}, driverId={}", bookingId, driverId);
        Booking booking = findBookingById(bookingId);

        if (!booking.getDriver().getId().equals(driverId)) {
            throw new UnauthorizedActionException(ErrorMessages.UNAUTHORIZED_BOOKING_ACTION);
        }

        repository.delete(booking);
        log.info("Booking deleted by driver: bookingId={}, driverId={}", bookingId, driverId);
    }

    @Transactional(readOnly = true)
    public Page<BookingDTO> getDriverBookings(UUID driverId, Pageable pageable) {
        log.debug("Fetching bookings for driver: driverId={}", driverId);
        return repository.findByDriverId(driverId, pageable)
                .map(BookingMapper::toDto);
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
        if (startTime.isBefore(Instant.now())) {
            throw new InvalidBookingTimeException(ErrorMessages.BOOKING_START_IN_PAST);
        }

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

