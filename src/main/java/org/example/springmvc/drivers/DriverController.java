package org.example.springmvc.drivers;

import jakarta.validation.Valid;
import org.example.springmvc.drivers.dto.CreateDriverDTO;
import org.example.springmvc.drivers.dto.DriverDTO;
import org.example.springmvc.drivers.dto.UpdateDriverDTO;
import org.example.springmvc.users.model.User;
import org.example.springmvc.users.UserServiceImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("drivers")
public class DriverController {

    private final DriverServiceImpl driverService;
    private final UserServiceImpl userService;

    public DriverController(DriverServiceImpl driverService, UserServiceImpl userService) {
        this.driverService = driverService;
        this.userService = userService;
    }

    @GetMapping
    public String list(
            @PageableDefault(value = 5) Pageable pageable,
            @ModelAttribute DriverFilter filter,
            Model model
    ) {
        var drivers = driverService.search(pageable, filter);
        model.addAttribute("drivers", drivers);
        model.addAttribute("filter", filter);
        return "drivers/list";
    }


    @GetMapping("new")
    public String createForm(Model model) {
        model.addAttribute("driver", new CreateDriverDTO(null, null, null));
        return "drivers/create";
    }

    @PostMapping("new")
    public String create(
            @Valid @ModelAttribute("driver") CreateDriverDTO dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "drivers/create";
        }

        User currentUser = userService.getCurrentUser();

        driverService.becomeDriver(currentUser.getId(), dto);
        redirectAttributes.addFlashAttribute("success", "You are now a driver");

        return "redirect:/";
    }

    @GetMapping("{id}/update")
    public String updateForm(@PathVariable UUID id, Model model) {
        DriverDTO driver = driverService.getById(id);
        model.addAttribute("driver", new UpdateDriverDTO(driver.fname(), driver.lname(), driver.ssn()));
        model.addAttribute("isUpdate", true);
        return "drivers/update";
    }

    @PostMapping("{id}/update")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("driver") UpdateDriverDTO dto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isUpdate", true);
            return "drivers/update";
        }

        driverService.update(id, dto);
        redirectAttributes.addFlashAttribute("success", "Driver updated");
        return "redirect:/drivers";
    }

    @PostMapping("{id}/delete")
    public String delete(@PathVariable UUID id,
                         RedirectAttributes redirectAttributes) {
        driverService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Driver deleted");
        return "redirect:/drivers";
    }
}
