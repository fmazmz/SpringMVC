package org.example.springmvc.bookings;

import jakarta.validation.Valid;
import org.example.springmvc.bookings.dto.BookingDTO;
import org.example.springmvc.bookings.dto.CreateBookingDTO;
import org.example.springmvc.bookings.model.BookingFilter;
import org.example.springmvc.cars.CarService;
import org.example.springmvc.drivers.DriverService;
import org.example.springmvc.insurances.InsuranceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final CarService carService;
    private final DriverService driverService;

    public BookingController(BookingService bookingService, CarService carService, DriverService driverService) {
        this.bookingService = bookingService;
        this.carService = carService;
        this.driverService = driverService;
    }

    @GetMapping
    public String listBookings(@PageableDefault(value = 5) Pageable pageable, @ModelAttribute BookingFilter filter, Model model) {
        Page<BookingDTO> bookings = bookingService.search(pageable, filter);
        model.addAttribute("bookings", bookings);
        model.addAttribute("filter", filter);
        return "bookings/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        var drivers = driverService.getAllPageable(Pageable.unpaged()).getContent();

        if (drivers.isEmpty()) {
            model.addAttribute("error", "No drivers found. Please add a driver first.");
        }

        CreateBookingDTO bookingDTO = new CreateBookingDTO(
                null,
                null,
                null,
                null,
                null
        );

        Map<InsuranceType, String> insuranceDisplayNames = Map.of(
                InsuranceType.BASIC, "Basic",
                InsuranceType.PREMIUM, "Premium",
                InsuranceType.FULL_COVERAGE, "Full Coverage"
        );

        model.addAttribute("booking", bookingDTO);
        model.addAttribute("drivers", drivers);
        model.addAttribute("insuranceTypes", insuranceDisplayNames);

        return "bookings/create";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("booking") CreateBookingDTO booking,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        if (bindingResult.hasErrors()) {

            model.addAttribute("drivers",
                    driverService.getAllPageable(Pageable.unpaged()).getContent());

            model.addAttribute("insuranceTypes", Map.of(
                    InsuranceType.BASIC, "Basic",
                    InsuranceType.PREMIUM, "Premium",
                    InsuranceType.FULL_COVERAGE, "Full Coverage"
            ));

            return "bookings/create";
        }

        bookingService.create(booking);
        redirectAttributes.addFlashAttribute("success", "Booking created successfully!");
        return "redirect:/bookings";
    }
}