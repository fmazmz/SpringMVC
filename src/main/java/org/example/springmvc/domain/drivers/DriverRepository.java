package org.example.springmvc.domain.drivers;

import org.example.springmvc.domain.drivers.model.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DriverRepository extends ListCrudRepository<Driver, UUID> {
    Page<Driver> findAll(Pageable pageable);
}
