package org.example.springmvc.cars;

import org.example.springmvc.cars.dto.CarDTO;
import org.example.springmvc.cars.dto.CreateCarDTO;
import org.example.springmvc.cars.dto.UpdateCarDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import java.util.UUID;

@Controller
@RequestMapping("cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public String list(
            @PageableDefault Pageable pageable,
            @ModelAttribute CarFilter filter,
            Model model
    ) {

        var cars = carService.search(pageable, filter);

        model.addAttribute("cars", cars);
        model.addAttribute("filter", filter);

        return "cars/list";
    }

    @GetMapping("new")
    public String createForm(Model model) {
        model.addAttribute(
                "car",
                new CreateCarDTO(null, null, null, null, null, null)
        );

        return "cars/create";
    }

    @PostMapping("new")
    public String create(
            @Valid @ModelAttribute("car") CreateCarDTO car,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            return "cars/create";
        }

        carService.create(car);

        redirectAttributes.addFlashAttribute(
                "success",
                "Car created"
        );

        return "redirect:/cars";
    }

    @GetMapping("{id}")
    public String view(@PathVariable UUID id, Model model) {
        CarDTO car = carService.getById(id);
        model.addAttribute("car", car);
        return "cars/view";
    }

    @GetMapping("{id}/update")
    public String updateForm(@PathVariable UUID id, Model model) {
        CarDTO car = carService.getById(id);
        model.addAttribute("car", car);
        return "cars/update";
    }

    @PostMapping("{id}/update")
    public String update(
            @PathVariable UUID id,
            @Valid @ModelAttribute("car") UpdateCarDTO carDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "cars/update";
        }

        carService.update(id, carDto);
        redirectAttributes.addFlashAttribute("success", "Car updated");
        return "redirect:/cars";
    }

    @PostMapping("{id}/delete")
    public String delete(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        carService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Car deleted");
        return "redirect:/cars";
    }
}