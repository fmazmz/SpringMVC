package org.example.springmvc.cars;

import org.example.springmvc.cars.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarRepository extends ListCrudRepository<Car, UUID> {
    Page<Car> findByMakeIgnoreCase(String make, Pageable pageable);
    Page<Car> findAll(Pageable pageable);

    Optional<Car> findByLicencePlateIgnoreCase(String licencePlate);
    Optional<Car> findByVinIgnoreCase(String vin);

    @Query("""
SELECT c
FROM Car c
WHERE NOT EXISTS (
    SELECT b
    FROM Booking b
    WHERE b.car = c
    AND b.startTime < :endTime
    AND b.endTime > :startTime
)
""")
    List<Car> findAvailableCars(Instant startTime, Instant endTime);
}
