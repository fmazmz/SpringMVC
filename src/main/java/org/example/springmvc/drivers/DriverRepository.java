package org.example.springmvc.drivers;

import org.example.springmvc.drivers.model.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends ListCrudRepository<Driver, UUID> {
    Page<Driver> findAll(Pageable pageable);
    Optional<Driver> findBySsn(String ssn);

    @Query("""
        SELECT d FROM Driver d WHERE (
            :q IS NULL OR
            LOWER(d.fname) LIKE :q OR
            LOWER(d.lname) LIKE :q OR
            LOWER(d.ssn) LIKE :q
        )
        AND (:fname IS NULL OR LOWER(d.fname) LIKE :fname)
        AND (:lname IS NULL OR LOWER(d.lname) LIKE :lname)
        AND (:ssn IS NULL OR LOWER(d.ssn) LIKE :ssn)
        AND (:driverId IS NULL OR d.id = :driverId)
    """)
    Page<Driver> searchDrivers(
            String q,
            String fname,
            String lname,
            String ssn,
            UUID driverId,
            Pageable pageable
    );
}
