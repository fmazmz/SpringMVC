package org.example.springmvc.booking.repository;

import org.example.springmvc.booking.model.Booking;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookingRepository extends ListCrudRepository<Booking, UUID> {
}