package org.example.springmvc.users;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("signup")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/new")
    public String signupForm(Model model) {
        CreateUserDTO dto = new CreateUserDTO(null, null);
        model.addAttribute("user", dto);

        return "users/signup";
    }

    @PostMapping
    public String signup(
            @Valid @ModelAttribute("user") CreateUserDTO user,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "users/signup";
        }

        userService.create(user);
        redirectAttributes.addFlashAttribute("success", "Account created!");

        return "redirect:/";
    }
}
