package org.example.springmvc.cars.repository;

import org.example.springmvc.cars.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarRepository extends ListCrudRepository<Car, UUID> {
    Page<Car> findByMakeIgnoreCase(String make, Pageable pageable);
    Page<Car> findAll(Pageable pageable);

    Optional<Car> findByLicencePlateIgnoreCase(String licencePlate);
    Optional<Car> findByVinIgnoreCase(String vin);
}
