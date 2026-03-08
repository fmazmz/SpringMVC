package org.example.springmvc.cars;

import jakarta.validation.Valid;
import org.example.springmvc.cars.dto.CarDTO;
import org.example.springmvc.cars.dto.CreateCarDTO;
import org.example.springmvc.cars.service.CarService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("cars")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public String listCars(
            @PageableDefault(value = 5, sort = "year") Pageable pageable,
            @RequestParam(required = false) String make,
            Model model) {

        Page<CarDTO> cars = make == null
                ? carService.getAll(pageable)
                : carService.getByMake(make, pageable);

        model.addAttribute("cars", cars);
        model.addAttribute("make", make);
        return "cars/list-cars";
    }

    @GetMapping("new")
    public String getCreationForm(Model model) {
        model.addAttribute("car", new CreateCarDTO(null, null, null, null, null, null));
        return "cars/create-car";
    }

    @PostMapping
    public String createCar(
            @Valid @ModelAttribute("car") CreateCarDTO car,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "cars/create-car";
        }

        carService.create(car);
        redirectAttributes.addFlashAttribute("success", "Car created successfully!");
        return "redirect:/cars";
    }
}
