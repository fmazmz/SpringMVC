package org.example.springmvc.domain.booking;

import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface BookingRepository extends ListCrudRepository<Booking, UUID> {
}