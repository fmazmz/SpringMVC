package org.example.springmvc.bookings;

import org.example.springmvc.bookings.dto.BookingDTO;
import org.example.springmvc.cars.CarService;
import org.example.springmvc.drivers.DriverService;
import org.example.springmvc.drivers.dto.DriverDTO;
import org.example.springmvc.drivers.model.Driver;
import org.example.springmvc.insurances.InsuranceType;
import org.example.springmvc.users.UserService;
import org.example.springmvc.users.model.User;
import org.example.springmvc.users.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private CarService carService;

    @MockitoBean
    private DriverService driverService;

    @MockitoBean
    private UserService userService;

    private BookingDTO sampleBookingDTO() {
        return new BookingDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Volvo",
                "XC90",
                Instant.now().plus(1, ChronoUnit.DAYS),
                Instant.now().plus(1, ChronoUnit.DAYS).plus(3, ChronoUnit.HOURS),
                InsuranceType.BASIC,
                new BigDecimal("379.00")
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void list_shouldReturnListViewWithBookingsAttribute() throws Exception {
        var page = new PageImpl<>(List.of(sampleBookingDTO()));
        when(bookingService.search(any(Pageable.class), any(BookingFilter.class))).thenReturn(page);

        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(view().name("bookings/list"))
                .andExpect(model().attributeExists("bookings"))
                .andExpect(model().attributeExists("filter"))
                .andExpect(model().attributeExists("insuranceTypes"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void view_shouldReturnViewWithBookingAttribute() throws Exception {
        BookingDTO booking = sampleBookingDTO();
        when(bookingService.getById(booking.id())).thenReturn(booking);

        User adminUser = new User();
        adminUser.setRole(UserRole.ADMIN);
        when(userService.getCurrentUser()).thenReturn(adminUser);

        mockMvc.perform(get("/bookings/{id}", booking.id()))
                .andExpect(status().isOk())
                .andExpect(view().name("bookings/view"))
                .andExpect(model().attributeExists("booking"))
                .andExpect(model().attribute("booking", booking));
    }

    @Test
    @WithMockUser(roles = "DRIVER")
    void createForm_shouldReturnCreateViewWithAttributes() throws Exception {
        User user = new User();
        Driver driver = new Driver("Erik", "Svensson", "1990010112");
        driver.setId(UUID.randomUUID());
        user.setDriver(driver);
        when(userService.getCurrentUser()).thenReturn(user);

        mockMvc.perform(get("/bookings/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("bookings/create"))
                .andExpect(model().attributeExists("booking"))
                .andExpect(model().attributeExists("insuranceTypes"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldRedirectOnSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/bookings/{id}/delete", id)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bookings"));

        verify(bookingService).delete(id);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateForm_shouldReturnUpdateViewWithAttributes() throws Exception {
        BookingDTO booking = sampleBookingDTO();
        when(bookingService.getById(booking.id())).thenReturn(booking);

        when(carService.getById(booking.carId())).thenReturn(
                new org.example.springmvc.cars.dto.CarDTO(
                        booking.carId(), "Volvo", "XC90",
                        new BigDecimal("100.00"), "ABC123", "VIN123456",
                        java.time.Year.of(2022)
                )
        );
        when(driverService.getById(booking.driverId())).thenReturn(
                new DriverDTO(booking.driverId(), "Erik", "Svensson", "1990010112", List.of())
        );

        mockMvc.perform(get("/bookings/{id}/update", booking.id()))
                .andExpect(status().isOk())
                .andExpect(view().name("bookings/update"))
                .andExpect(model().attributeExists("booking"))
                .andExpect(model().attributeExists("bookingId"))
                .andExpect(model().attributeExists("carDisplay"))
                .andExpect(model().attributeExists("driverDisplay"))
                .andExpect(model().attributeExists("insuranceTypes"))
                .andExpect(model().attribute("isUpdate", true));
    }

    @Test
    void list_shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isUnauthorized());
    }
}

