package org.example.springmvc.cars;

import org.example.springmvc.cars.dto.CarDTO;
import org.example.springmvc.cars.dto.CarFilter;
import org.example.springmvc.cars.dto.CreateCarDTO;
import org.example.springmvc.cars.dto.UpdateCarDTO;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Controller
@RequestMapping("cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public String root() {
        return "redirect:/cars/browse";
    }

    @GetMapping("browse")
    public String browse(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute CarFilter filter,
            @RequestParam(required = false) String sort,
            Model model
    ) {
        log.debug("GET /cars/browse - page={}, sort={}", pageable.getPageNumber(), sort);
        boolean hasSearch = (filter.q() != null && !filter.q().isBlank())
                || (filter.make() != null && !filter.make().isBlank())
                || (filter.model() != null && !filter.model().isBlank())
                || filter.year() != null
                || filter.minPrice() != null
                || filter.maxPrice() != null
                || (filter.licencePlate() != null && !filter.licencePlate().isBlank())
                || (filter.vin() != null && !filter.vin().isBlank())
                || (sort != null && !sort.isBlank());

        model.addAttribute("filter", filter);
        model.addAttribute("hasSearch", hasSearch);

        var cars = carService.search(pageable, filter);
        model.addAttribute("cars", cars);

        return "cars/browse";
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
        log.debug("POST /cars/new - make={}, model={}", car.make(), car.model());

        if (bindingResult.hasErrors()) {
            log.debug("Validation errors on car creation: {}", bindingResult.getAllErrors());
            return "cars/create";
        }

        carService.create(car);
        redirectAttributes.addFlashAttribute("success", "Car created successfully");

        return "redirect:/cars/browse";
    }

    @GetMapping("{id}")
    public String view(@PathVariable UUID id, Model model) {
        log.debug("GET /cars/{}", id);
        CarDTO car = carService.getById(id);
        model.addAttribute("car", car);
        return "cars/view";
    }

    @GetMapping("{id}/update")
    public String updateForm(@PathVariable UUID id, Model model) {
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
    }

    @PostMapping("{id}/update")
    public String update(
            @PathVariable UUID id,
            @Valid @ModelAttribute("car") UpdateCarDTO carDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        log.debug("POST /cars/{}/update", id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("carId", id);
            return "cars/update";
        }

        carService.update(id, carDto);
        redirectAttributes.addFlashAttribute("success", "Car updated successfully");
        return "redirect:/cars/" + id;
    }

    @PostMapping("{id}/delete")
    public String delete(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        log.debug("POST /cars/{}/delete", id);
        carService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Car deleted successfully");
        return "redirect:/cars/browse";
    }
}
