package org.example.springmvc.cars;

import org.example.springmvc.cars.dto.CarDTO;
import org.example.springmvc.cars.dto.CarFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarService carService;

    private CarDTO sampleCarDTO() {
        return new CarDTO(
                UUID.randomUUID(), "Volvo", "XC90",
                new BigDecimal("150.00"), "ABC123", "VIN123456", Year.of(2022)
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void root_shouldRedirectToBrowse() throws Exception {
        mockMvc.perform(get("/cars"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cars/browse"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void browse_shouldReturnBrowseViewWithCarsAttribute() throws Exception {
        CarDTO car = sampleCarDTO();
        var page = new PageImpl<>(List.of(car));

        when(carService.search(any(Pageable.class), any(CarFilter.class))).thenReturn(page);

        mockMvc.perform(get("/cars/browse"))
                .andExpect(status().isOk())
                .andExpect(view().name("cars/browse"))
                .andExpect(model().attributeExists("cars"))
                .andExpect(model().attributeExists("filter"))
                .andExpect(model().attributeExists("hasSearch"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createForm_shouldReturnCreateViewWithCarAttribute() throws Exception {
        mockMvc.perform(get("/cars/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("cars/create"))
                .andExpect(model().attributeExists("car"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldRedirectOnSuccess() throws Exception {
        when(carService.create(any())).thenReturn(sampleCarDTO());

        mockMvc.perform(post("/cars/new")
                        .with(csrf())
                        .param("make", "Volvo")
                        .param("model", "XC90")
                        .param("hourlyPrice", "150.00")
                        .param("licencePlate", "ABC123")
                        .param("vin", "VIN123456")
                        .param("year", "2022"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cars/browse"));

        verify(carService).create(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnFormOnValidationError() throws Exception {
        mockMvc.perform(post("/cars/new")
                        .with(csrf())
                        .param("make", "")
                        .param("model", "")
                        .param("hourlyPrice", "")
                        .param("licencePlate", "")
                        .param("vin", "")
                        .param("year", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("cars/create"));

        verify(carService, never()).create(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void view_shouldReturnViewWithCarAttribute() throws Exception {
        CarDTO car = sampleCarDTO();
        when(carService.getById(car.id())).thenReturn(car);

        mockMvc.perform(get("/cars/{id}", car.id()))
                .andExpect(status().isOk())
                .andExpect(view().name("cars/view"))
                .andExpect(model().attributeExists("car"))
                .andExpect(model().attribute("car", car));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateForm_shouldReturnUpdateViewWithAttributes() throws Exception {
        CarDTO car = sampleCarDTO();
        when(carService.getById(car.id())).thenReturn(car);

        mockMvc.perform(get("/cars/{id}/update", car.id()))
                .andExpect(status().isOk())
                .andExpect(view().name("cars/update"))
                .andExpect(model().attributeExists("car"))
                .andExpect(model().attributeExists("carId"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldRedirectOnSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/cars/{id}/update", id)
                        .with(csrf())
                        .param("make", "BMW")
                        .param("model", "X5")
                        .param("hourlyPrice", "200.00")
                        .param("licencePlate", "DEF456")
                        .param("vin", "VIN999999")
                        .param("year", "2023"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cars/" + id));

        verify(carService).update(eq(id), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnFormOnValidationError() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/cars/{id}/update", id)
                        .with(csrf())
                        .param("make", "")
                        .param("model", "")
                        .param("hourlyPrice", "")
                        .param("licencePlate", "")
                        .param("vin", "")
                        .param("year", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("cars/update"))
                .andExpect(model().attributeExists("carId"));

        verify(carService, never()).update(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldRedirectOnSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/cars/{id}/delete", id)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cars/browse"));

        verify(carService).delete(id);
    }

    @Test
    void browse_shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/cars/browse"))
                .andExpect(status().isUnauthorized());
    }
}
