package org.example.springmvc.drivers;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DriverRepository extends ListCrudRepository<Driver, UUID> {
}
