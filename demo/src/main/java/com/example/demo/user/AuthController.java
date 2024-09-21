package com.example.demo.user;

import com.example.demo.registration.RegistrationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Maps to src/main/resources/templates/login.html
    }

    @GetMapping("/register")
    public String register() {
        return "register"; // Maps to src/main/resources/templates/register.html
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegistrationRequest request,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.signUpUser(
                    new User(
                            request.getFirstName(),
                            request.getLastName(),
                            request.getUsername(),
                            request.getEmail(),
                            request.getPassword(),
                            UserRole.USER
                    )
            );
            redirectAttributes.addFlashAttribute("success",
                    "Registration successful! Please log in.");
            return "redirect:/auth/login";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Registration failed: " + e.getMessage());
            return "redirect:/auth/register";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "An unexpected error occurred. Please try again.");
            return "redirect:/auth/register";
        }
    }
}

