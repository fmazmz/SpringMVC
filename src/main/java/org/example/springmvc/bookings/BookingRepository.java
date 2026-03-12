package org.example.springmvc.bookings;

import org.example.springmvc.bookings.model.Booking;
import org.example.springmvc.insurances.InsuranceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface BookingRepository extends ListCrudRepository<Booking, UUID> {

    Page<Booking> findByDriverId(UUID driverId, Pageable pageable);

    @Query("""
    SELECT COUNT(b) > 0
    FROM Booking b
    WHERE b.car.id = :carId
    AND b.startTime < :endTime
    AND b.endTime > :startTime
    """)
    boolean existsOverlappingBooking(UUID carId, Instant startTime, Instant endTime);


    @Query("""
    SELECT b FROM Booking b
    WHERE (:carId IS NULL OR b.car.id = :carId)
      AND (:driverId IS NULL OR b.driver.id = :driverId)
      AND (:insuranceType IS NULL OR b.insuranceType = :insuranceType)
    """)
    Page<Booking> searchBookings(
            @Param("carId") UUID carId,
            @Param("driverId") UUID driverId,
            @Param("insuranceType") InsuranceType insuranceType,
            Pageable pageable
    );

    @Query("""
    SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END 
    FROM Booking b 
    WHERE b.car.id = :carId 
    AND b.id != :excludeId
    AND (
        (b.startTime <= :endTime AND b.endTime >= :startTime)
    )
    """)
    boolean existsOverlappingBookingExcludingId(
            @Param("carId") UUID carId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            @Param("excludeId") UUID excludeId
    );
}