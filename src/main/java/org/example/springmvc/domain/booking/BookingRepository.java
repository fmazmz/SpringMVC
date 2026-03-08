package org.example.springmvc.domain.booking;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookingRepository extends ListCrudRepository<Booking, UUID> {
}