package org.example.springmvc.cars;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface CarRepository extends ListCrudRepository<Car, UUID> {
    Page<Car> findByMakeIgnoreCase(String make, Pageable pageable);

    Page<Car> findAll(Pageable pageable);
}
