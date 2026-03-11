package org.example.springmvc.bookings;

import org.example.springmvc.bookings.dto.BookingDTO;
import org.example.springmvc.bookings.dto.CreateBookingDTO;
import org.example.springmvc.bookings.dto.UpdateBookingDTO;
import org.example.springmvc.bookings.model.BookingFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BookingService {

    Page<BookingDTO> search(Pageable pageable, BookingFilter filter);

    BookingDTO getById(UUID id);

    void create(CreateBookingDTO dto);

    void update(UUID id, UpdateBookingDTO dto);

    void delete(UUID id);

    void deleteByDriver(UUID bookingId, UUID driverId);

    Page<BookingDTO> getDriverBookings(UUID driverId, Pageable pageable);
}