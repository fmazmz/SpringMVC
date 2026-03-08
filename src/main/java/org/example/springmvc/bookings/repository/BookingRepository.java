package org.example.springmvc.bookings.repository;

import org.example.springmvc.bookings.model.Booking;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookingRepository extends ListCrudRepository<Booking, UUID> {
}