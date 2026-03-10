package org.example.springmvc.cars;

import org.example.springmvc.cars.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarRepository extends ListCrudRepository<Car, UUID> {

    Optional<Car> findByVinIgnoreCase(String vin);

    Optional<Car> findByLicencePlateIgnoreCase(String licencePlate);

    boolean existsByLicencePlateIgnoreCaseAndIdNot(String licencePlate, UUID id);

    boolean existsByVinIgnoreCaseAndIdNot(String vin, UUID id);

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

    @Query("""
SELECT c FROM Car c WHERE (
      :q IS NULL OR
      LOWER(c.make) LIKE :q OR
      LOWER(c.model) LIKE :q OR
      LOWER(c.licencePlate) LIKE :q OR
      LOWER(c.vin) LIKE :q
)
AND (:make IS NULL OR LOWER(c.make) LIKE :make)
AND (:model IS NULL OR LOWER(c.model) LIKE :model)
AND (:year IS NULL OR c.year = :year)
AND (:licencePlate IS NULL OR LOWER(c.licencePlate) LIKE :licencePlate)
AND (:vin IS NULL OR LOWER(c.vin) LIKE :vin)
""")
    Page<Car> searchCars(
            String q,
            String make,
            String model,
            Year year,
            String licencePlate,
            String vin,
            Pageable pageable
    );
}