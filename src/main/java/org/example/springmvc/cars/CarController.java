package org.example.springmvc.cars;

import org.example.springmvc.cars.dto.CreateCarDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public String listCars(
            @PageableDefault Pageable pageable,
            @ModelAttribute CarFilter filter,
            Model model
    ) {

        var cars = carService.search(pageable, filter);

        model.addAttribute("cars", cars);
        model.addAttribute("filter", filter);

        return "cars/list";
    }

    @GetMapping("/new")
    public String getCreationForm(Model model) {
        model.addAttribute(
                "car",
                new CreateCarDTO(null, null, null, null, null, null)
        );

        return "cars/create";
    }

    @PostMapping
    public String createCar(
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
                "Car created successfully!"
        );

        return "redirect:/cars";
    }
}