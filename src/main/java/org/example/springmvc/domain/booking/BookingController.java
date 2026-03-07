package org.example.springmvc.domain.booking;

import org.example.springmvc.domain.booking.dto.CreateBookingDTO;
import org.example.springmvc.domain.cars.CarService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final CarService carService;

    public BookingController(BookingService bookingService, CarService carService) {
        this.bookingService = bookingService;
        this.carService = carService;
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("booking", new CreateBookingDTO(null,null,null,null,null));
        model.addAttribute("cars", carService.getAll(Pageable.unpaged()).getContent());
        return "bookings/create-booking";
    }

    @PostMapping
    public String create(@ModelAttribute CreateBookingDTO booking) {
        bookingService.create(booking);
        return "redirect:/cars";
    }
}