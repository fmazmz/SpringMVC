package org.example.springmvc.bookings;

import jakarta.validation.Valid;
import org.example.springmvc.bookings.dto.BookingDTO;
import org.example.springmvc.bookings.dto.CreateBookingDTO;
import org.example.springmvc.bookings.dto.UpdateBookingDTO;
import org.example.springmvc.bookings.model.BookingFilter;
import org.example.springmvc.cars.CarService;
import org.example.springmvc.drivers.DriverService;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.insurances.InsuranceType;
import org.example.springmvc.users.UserService;
import org.example.springmvc.users.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("bookings")
public class BookingController {

    private final BookingService bookingService;
    private final CarService carService;
    private final DriverService driverService;
    private final UserService userService;

    public BookingController(BookingService bookingService, CarService carService, DriverService driverService, UserService userService) {
        this.bookingService = bookingService;
        this.carService = carService;
        this.driverService = driverService;
        this.userService = userService;
    }

    @GetMapping
    public String list(@PageableDefault(value = 5) Pageable pageable,
                               @ModelAttribute BookingFilter filter,
                               Model model) {
        Page<BookingDTO> bookings = bookingService.search(pageable, filter);
        model.addAttribute("bookings", bookings);
        model.addAttribute("filter", filter);
        return "bookings/list";
    }

    @GetMapping("{id}")
    public String view(@PathVariable UUID id, Model model) {
        BookingDTO booking = bookingService.getById(id);
        model.addAttribute("booking", booking);
        return "bookings/view";
    }

    @GetMapping("new")
    public String createForm(@RequestParam(required = false) Instant startTime,
                             @RequestParam(required = false) Instant endTime,
                             Model model) {
        User user = userService.getCurrentUser();
        Driver driver = user.getDriver();

        CreateBookingDTO bookingDTO = new CreateBookingDTO(
                null,
                driver != null ? driver.getId() : null,
                startTime,
                endTime,
                null
        );

        model.addAttribute("booking", bookingDTO);
        model.addAttribute("insuranceTypes", insuranceDisplayNames());

        if (driver == null) {
            model.addAttribute("error", "You must become a driver first.");
            return "bookings/create";
        }

        if (startTime != null && endTime != null) {
            if (!startTime.isBefore(endTime)) {
                model.addAttribute("error", "Start time must be before end time.");
            } else {
                model.addAttribute("cars", carService.findAvailable(startTime, endTime));
            }
        }

        return "bookings/create";
    }

    @PostMapping("new")
    public String create(@Valid @ModelAttribute("booking") CreateBookingDTO booking,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        if (bindingResult.hasErrors()) {
            if (booking.startTime() != null && booking.endTime() != null && booking.startTime().isBefore(booking.endTime())) {
                model.addAttribute("cars", carService.findAvailable(booking.startTime(), booking.endTime()));
            }
            model.addAttribute("insuranceTypes", insuranceDisplayNames());
            return "bookings/create";
        }

        bookingService.create(booking);
        redirectAttributes.addFlashAttribute("success", "Booking created");
        return "redirect:/";
    }

    @GetMapping("{id}/update")
    public String updateForm(@PathVariable UUID id, Model model) {

        BookingDTO booking = bookingService.getById(id);
        UpdateBookingDTO updateDto = new UpdateBookingDTO(
                booking.carId(),
                booking.driverId(),
                booking.startTime(),
                booking.endTime(),
                booking.insuranceType()
        );

        model.addAttribute("booking", updateDto);
        model.addAttribute("insuranceTypes", insuranceDisplayNames());
        model.addAttribute("isUpdate", true);
        return "bookings/update";
    }

    @PostMapping("{id}/update")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("booking") UpdateBookingDTO booking,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("insuranceTypes", insuranceDisplayNames());
            model.addAttribute("isUpdate", true);
            return "bookings/update";
        }

        bookingService.update(id, booking);
        redirectAttributes.addFlashAttribute("success", "Booking updated");
        return "redirect:/bookings";
    }

    @PostMapping("{id}/delete")
    public String delete(@PathVariable UUID id,
                         RedirectAttributes redirectAttributes) {

        bookingService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Booking deleted");
        return "redirect:/bookings";
    }

    private Map<InsuranceType, String> insuranceDisplayNames() {
        return Map.of(
                InsuranceType.BASIC, "Basic",
                InsuranceType.PREMIUM, "Premium",
                InsuranceType.FULL_COVERAGE, "Full Coverage"
        );
    }
}