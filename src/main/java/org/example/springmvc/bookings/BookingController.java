package org.example.springmvc.bookings;

import jakarta.validation.Valid;
import org.example.springmvc.bookings.dto.BookingDTO;
import org.example.springmvc.bookings.dto.CreateBookingDTO;
import org.example.springmvc.bookings.dto.UpdateBookingDTO;
import org.example.springmvc.cars.CarService;
import org.example.springmvc.cars.dto.CarDTO;
import org.example.springmvc.drivers.DriverService;
import org.example.springmvc.drivers.dto.DriverDTO;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.exceptions.ErrorMessages;
import org.example.springmvc.exceptions.UnauthorizedActionException;
import org.example.springmvc.insurances.InsuranceType;
import org.example.springmvc.users.UserService;
import org.example.springmvc.users.model.User;
import org.example.springmvc.users.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("bookings")
public class BookingController {

    private final BookingService bookingService;
    private final CarService carService;
    private final DriverService driverService;
    private final UserService userService;

    public BookingController(
            BookingService bookingService,
            CarService carService,
            DriverService driverService,
            UserService userService
    ) {
        this.bookingService = bookingService;
        this.carService = carService;
        this.driverService = driverService;
        this.userService = userService;
    }

    @GetMapping
    public String list(
            @PageableDefault(size = 5, sort = "startTime", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute BookingFilter filter,
            Model model
    ) {
        Page<BookingDTO> bookings = bookingService.search(pageable, filter);

        model.addAttribute("bookings", bookings);
        model.addAttribute("filter", filter);
        model.addAttribute("insuranceTypes", InsuranceType.values());

        return "bookings/list";
    }

    @GetMapping("my-bookings")
    public String driverBookings(
            @PageableDefault(value = 5) Pageable pageable,
            Model model
    ) {
        User user = userService.getCurrentUser();
        Driver driver = user.getDriver();

        if (driver == null) {
            throw new UnauthorizedActionException("You are not registered as a driver.");
        }

        Page<BookingDTO> bookings =
                bookingService.getDriverBookings(driver.getId(), pageable);

        model.addAttribute("bookings", bookings);

        return "bookings/my-bookings";
    }

    @GetMapping("{id}")
    public String view(@PathVariable UUID id, Model model) {

        BookingDTO booking = bookingService.getById(id);
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole() != UserRole.ADMIN) {

            Driver driver = currentUser.getDriver();

            if (driver == null || !driver.getId().equals(booking.driverId())) {
                throw new UnauthorizedActionException(
                        ErrorMessages.UNAUTHORIZED_BOOKING_ACTION
                );
            }
        }

        model.addAttribute("booking", booking);

        return "bookings/view";
    }

    @GetMapping("new")
    public String createForm(
            @RequestParam(required = false) Instant startTime,
            @RequestParam(required = false) Instant endTime,
            Model model
    ) {

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
                model.addAttribute("error", "Start time must be before end time");
            } else {
                model.addAttribute(
                        "cars",
                        carService.findAvailable(startTime, endTime)
                );
            }
        }

        return "bookings/create";
    }

    @PostMapping("new")
    public String create(
            @Valid @ModelAttribute("booking") CreateBookingDTO booking,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {

        User user = userService.getCurrentUser();
        Driver driver = user.getDriver();

        if (driver == null) {
            throw new UnauthorizedActionException(
                    "You are not registered as a driver."
            );
        }

        if (bindingResult.hasErrors()) {

            if (booking.startTime() != null &&
                    booking.endTime() != null &&
                    booking.startTime().isBefore(booking.endTime())) {

                model.addAttribute(
                        "cars",
                        carService.findAvailable(
                                booking.startTime(),
                                booking.endTime()
                        )
                );
            }

            model.addAttribute("insuranceTypes", insuranceDisplayNames());

            return "bookings/create";
        }

        try {

            CreateBookingDTO safeBooking = new CreateBookingDTO(
                    booking.carId(),
                    driver.getId(),
                    booking.startTime(),
                    booking.endTime(),
                    booking.insuranceType()
            );

            bookingService.create(safeBooking);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Booking created"
            );

            return "redirect:/";

        } catch (Exception e) {

            model.addAttribute("booking", booking);
            model.addAttribute("error", e.getMessage());

            if (booking.startTime() != null &&
                    booking.endTime() != null &&
                    booking.startTime().isBefore(booking.endTime())) {

                model.addAttribute(
                        "cars",
                        carService.findAvailable(
                                booking.startTime(),
                                booking.endTime()
                        )
                );
            }

            model.addAttribute("insuranceTypes", insuranceDisplayNames());

            return "bookings/create";
        }
    }

    @GetMapping("{id}/update")
    public String updateForm(@PathVariable UUID id, Model model) {

        try {

            BookingDTO booking = bookingService.getById(id);

            UpdateBookingDTO updateDto = new UpdateBookingDTO(
                    booking.carId(),
                    booking.driverId(),
                    booking.startTime(),
                    booking.endTime(),
                    booking.insuranceType()
            );

            prepareUpdateForm(model, id, updateDto, booking);

            return "bookings/update";

        } catch (Exception e) {

            model.addAttribute(
                    "error",
                    "Error loading booking: " + e.getMessage()
            );

            return "redirect:/bookings";
        }
    }

    @PostMapping("{id}/update")
    public String update(
            @PathVariable UUID id,
            @Valid @ModelAttribute("booking") UpdateBookingDTO bookingDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {

        BookingDTO originalBooking = bookingService.getById(id);

        if (bindingResult.hasErrors()) {

            prepareUpdateForm(model, id, bookingDto, originalBooking);

            return "bookings/update";
        }

        try {

            bookingService.update(id, bookingDto);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Booking updated successfully"
            );

            return "redirect:/bookings";

        } catch (Exception e) {

            prepareUpdateForm(model, id, bookingDto, originalBooking);

            model.addAttribute("error", e.getMessage());

            return "bookings/update";
        }
    }

    @PostMapping("{id}/delete")
    public String delete(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {

        try {

            bookingService.delete(id);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Booking deleted"
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Failed to delete booking: " + e.getMessage()
            );
        }

        return "redirect:/bookings";
    }

    @PostMapping("my-bookings/{id}/delete")
    public String deleteMyBooking(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {

        try {

            User user = userService.getCurrentUser();
            Driver driver = user.getDriver();

            if (driver == null) {
                throw new UnauthorizedActionException(
                        "You are not registered as a driver."
                );
            }

            bookingService.deleteByDriver(id, driver.getId());

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Booking cancelled"
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Failed to cancel booking: " + e.getMessage()
            );
        }

        return "redirect:/bookings/my-bookings";
    }

    private void prepareUpdateForm(
            Model model,
            UUID bookingId,
            UpdateBookingDTO bookingDto,
            BookingDTO originalBooking
    ) {

        CarDTO car = carService.getById(originalBooking.carId());

        String carDisplay =
                car.make() + " " + car.model() + " (" + car.licencePlate() + ")";

        DriverDTO driver =
                driverService.getById(originalBooking.driverId());

        String driverDisplay =
                driver.fname() + " " + driver.lname();

        String formattedStartTime =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
                        .format(originalBooking.startTime()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime());

        String formattedEndTime =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
                        .format(originalBooking.endTime()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime());

        model.addAttribute("booking", bookingDto);
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("carDisplay", carDisplay);
        model.addAttribute("driverDisplay", driverDisplay);
        model.addAttribute("formattedStartTime", formattedStartTime);
        model.addAttribute("formattedEndTime", formattedEndTime);
        model.addAttribute("insuranceTypes", insuranceDisplayNames());
        model.addAttribute("isUpdate", true);
    }

    private Map<InsuranceType, String> insuranceDisplayNames() {

        return Map.of(
                InsuranceType.BASIC, "Basic",
                InsuranceType.PREMIUM, "Premium",
                InsuranceType.FULL_COVERAGE, "Full Coverage"
        );
    }
}