package org.example.springmvc.drivers;

import org.example.springmvc.drivers.dto.DriverDTO;
import org.example.springmvc.drivers.dto.DriverFilter;
import org.example.springmvc.users.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DriverController.class)
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DriverService driverService;

    @MockitoBean
    private UserService userService;

    private DriverDTO sampleDriverDTO() {
        return new DriverDTO(
                UUID.randomUUID(), "Erik", "Svensson", "1990010112",
                List.of()
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void list_shouldReturnListViewWithDriversAttribute() throws Exception {
        var page = new PageImpl<>(List.of(sampleDriverDTO()));
        when(driverService.search(any(Pageable.class), any(DriverFilter.class))).thenReturn(page);

        mockMvc.perform(get("/drivers"))
                .andExpect(status().isOk())
                .andExpect(view().name("drivers/list"))
                .andExpect(model().attributeExists("drivers"))
                .andExpect(model().attributeExists("filter"));
    }

    @Test
    @WithMockUser(roles = "APP_USER")
    void createForm_shouldReturnCreateViewWithDriverAttribute() throws Exception {
        mockMvc.perform(get("/drivers/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("drivers/create"))
                .andExpect(model().attributeExists("driver"));
    }

    @Test
    @WithMockUser(roles = "APP_USER")
    void create_shouldReturnFormOnValidationError() throws Exception {
        mockMvc.perform(post("/drivers/new")
                        .with(csrf())
                        .param("fname", "")
                        .param("lname", "")
                        .param("ssn", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("drivers/create"));

        verify(driverService, never()).becomeDriver(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateForm_shouldReturnUpdateViewWithAttributes() throws Exception {
        DriverDTO driver = sampleDriverDTO();
        when(driverService.getById(driver.id())).thenReturn(driver);

        mockMvc.perform(get("/drivers/{id}/update", driver.id()))
                .andExpect(status().isOk())
                .andExpect(view().name("drivers/update"))
                .andExpect(model().attributeExists("driver"))
                .andExpect(model().attributeExists("driverId"))
                .andExpect(model().attribute("isUpdate", true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldRedirectOnSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/drivers/{id}/update", id)
                        .with(csrf())
                        .param("fname", "Anna")
                        .param("lname", "Karlsson")
                        .param("ssn", "1985050534"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/drivers"));

        verify(driverService).update(eq(id), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnFormOnValidationError() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/drivers/{id}/update", id)
                        .with(csrf())
                        .param("fname", "")
                        .param("lname", "")
                        .param("ssn", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("drivers/update"))
                .andExpect(model().attributeExists("driverId"))
                .andExpect(model().attribute("isUpdate", true));

        verify(driverService, never()).update(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldRedirectOnSuccess() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/drivers/{id}/delete", id)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/drivers"));

        verify(driverService).delete(id);
    }

    @Test
    void list_shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/drivers"))
                .andExpect(status().isUnauthorized());
    }
}
