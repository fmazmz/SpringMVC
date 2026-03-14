package org.example.springmvc.cars;

import org.example.springmvc.cars.dto.CarDTO;
import org.example.springmvc.cars.dto.CreateCarDTO;
import org.example.springmvc.cars.dto.UpdateCarDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.UUID;

@Controller
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/cars/browse";
    }

    @GetMapping("/browse")
    public String browse(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute CarFilter filter,
            @RequestParam(required = false) String sort,
            Model model
    ) {
        boolean hasSearch = hasBrowseInput(filter) || hasSort(sort);

        model.addAttribute("filter", filter);
        model.addAttribute("hasSearch", hasSearch);

        var cars = carService.search(pageable, filter);
        model.addAttribute("cars", cars);

        return "cars/browse";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute(
                "car",
                new CreateCarDTO(null, null, null, null, null, null)
        );

        return "cars/create";
    }

    @PostMapping("/new")
    public String create(
            @Valid @ModelAttribute("car") CreateCarDTO car,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "cars/create";
        }

        carService.create(car);
        redirectAttributes.addFlashAttribute("success", "Car created successfully");

        return "redirect:/cars/browse";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable UUID id, Model model) {
        try {
            CarDTO car = carService.getById(id);
            model.addAttribute("car", car);
            return "cars/view";
        } catch (Exception e) {
            model.addAttribute("error", "Car not found");
            return "redirect:/cars/browse";
        }
    }

    @GetMapping("/{id}/update")
    public String updateForm(@PathVariable UUID id, Model model) {
        try {
            CarDTO car = carService.getById(id);

            UpdateCarDTO dto = new UpdateCarDTO(
                    car.make(),
                    car.model(),
                    car.hourlyPrice(),
                    car.licencePlate(),
                    car.vin(),
                    car.year()
            );

            model.addAttribute("car", dto);
            model.addAttribute("carId", id);

            return "cars/update";
        } catch (Exception e) {
            model.addAttribute("error", "Car not found");
            return "redirect:/cars/browse";
        }
    }

    @PostMapping("/{id}/update")
    public String update(
            @PathVariable UUID id,
            @Valid @ModelAttribute("car") UpdateCarDTO carDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("carId", id);
            return "cars/update";
        }

        try {
            carService.update(id, carDto);
            redirectAttributes.addFlashAttribute("success", "Car updated successfully");
            return "redirect:/cars/" + id;
        } catch (Exception e) {
            model.addAttribute("error", "Error updating car: " + e.getMessage());
            model.addAttribute("carId", id);
            return "cars/update";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            carService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Car deleted successfully");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("foreign key constraint")) {
                redirectAttributes.addFlashAttribute("error",
                        "Cannot delete this car because it has existing bookings. Please delete all bookings for this car first.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error deleting car: " + e.getMessage());
            }
        }
        return "redirect:/cars/browse";
    }

    private boolean hasBrowseInput(CarFilter filter) {
        return notBlank(filter.q())
                || notBlank(filter.make())
                || notBlank(filter.model())
                || filter.year() != null
                || filter.minPrice() != null
                || filter.maxPrice() != null
                || notBlank(filter.licencePlate())
                || notBlank(filter.vin());
    }

    private boolean hasSort(String sort) {
        return sort != null && !sort.isBlank();
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}