package org.example.springmvc.bookings.repository;

import org.example.springmvc.bookings.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends ListCrudRepository<Booking, UUID> {
    Page<Booking> findAll(Pageable pageable);
    Page<Booking> findByCarId(Pageable pageable, UUID carId);
    Optional<Booking> findCarById(UUID carId);

    @Query("""
SELECT COUNT(b) > 0
FROM Booking b
WHERE b.car.id = :carId
AND b.startTime < :endTime
AND b.endTime > :startTime
""")
    boolean existsOverlappingBooking(UUID carId, Instant startTime, Instant endTime);
}