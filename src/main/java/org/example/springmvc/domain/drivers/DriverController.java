package org.example.springmvc.domain.drivers;

import org.example.springmvc.domain.drivers.model.dto.DriverDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping
    public String listDrivers(
            @PageableDefault(value = 5) Pageable pageable,
            Model model
            ) {
        Page<DriverDTO> drivers = driverService.getAllPageable(pageable);
        model.addAttribute("drivers", drivers);

        return "drivers/list-drivers";
    }
}
