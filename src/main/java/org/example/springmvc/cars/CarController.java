package org.example.springmvc.cars;

import org.example.springmvc.cars.dto.CarDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
}
