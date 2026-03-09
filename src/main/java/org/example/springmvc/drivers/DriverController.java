package org.example.springmvc.drivers;

import jakarta.validation.Valid;
import org.example.springmvc.drivers.dto.CreateDriverDTO;
import org.example.springmvc.users.model.User;
import org.example.springmvc.users.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("drivers")
public class DriverController {

    private final DriverService driverService;
    private final UserService userService;

    public DriverController(DriverService driverService, UserService userService) {
        this.driverService = driverService;
        this.userService = userService;
    }

    @GetMapping
    public String listDrivers(
            @PageableDefault(value = 5) Pageable pageable,
            @ModelAttribute DriverFilter filter,
            Model model
    ) {
        var drivers = driverService.search(pageable, filter);
        model.addAttribute("drivers", drivers);
        model.addAttribute("filter", filter);
        return "drivers/list";
    }


    @GetMapping("/new")
    public String showDriverForm(Model model) {
        model.addAttribute("driver", new CreateDriverDTO(null, null, null));
        return "drivers/create";
    }

    @PostMapping("/new")
    public String becomeDriver(
            @Valid @ModelAttribute("driver") CreateDriverDTO dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "drivers/create";
        }

        User currentUser = userService.getCurrentUser();

        driverService.becomeDriver(currentUser.getId(), dto);
        redirectAttributes.addFlashAttribute("success", "You are now a driver!");

        return "redirect:/";
    }
}
