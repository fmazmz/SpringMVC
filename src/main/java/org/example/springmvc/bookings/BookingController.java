package org.example.springmvc.bookings;

import org.example.springmvc.bookings.dto.CreateBookingDTO;
import org.example.springmvc.bookings.service.BookingService;
import org.example.springmvc.cars.service.CarService;
import org.example.springmvc.drivers.service.DriverService;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.insurances.InsuranceType;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.LinkedHashMap;
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

    @GetMapping("/new")
    public String createForm(Model model) {
        var optionalDriver = driverService.getAll().stream().findFirst();
        if (optionalDriver.isEmpty()) {
            model.addAttribute("error", "No drivers found. Please add a driver first.");
            return "error/no-drivers";
        }

        Driver driver = optionalDriver.get();

        CreateBookingDTO bookingDTO = new CreateBookingDTO(
                null,
                driver.getId(),
                null,
                null,
                null
        );

        Map<InsuranceType, String> insuranceDisplayNames = new LinkedHashMap<>();
        insuranceDisplayNames.put(InsuranceType.BASIC, "Basic");
        insuranceDisplayNames.put(InsuranceType.PREMIUM, "Premium");
        insuranceDisplayNames.put(InsuranceType.FULL_COVERAGE, "Full Coverage");

        model.addAttribute("booking", bookingDTO);
        model.addAttribute("cars", carService.getAll(Pageable.unpaged()).getContent());
        model.addAttribute("insuranceTypes", InsuranceType.values());

        return "bookings/create";
    }

    @PostMapping
    public String create(@ModelAttribute CreateBookingDTO booking, Model model) {
        try {
            bookingService.create(booking);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to create booking: " + e.getMessage());
            return "error-page";
        }
        return "redirect:/cars";
    }
}