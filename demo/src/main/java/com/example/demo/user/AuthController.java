package com.example.demo.user;

import com.example.demo.movies.MovieService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.demo.registration.RegistrationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final MovieService movieService;

    public AuthController(UserService userService, MovieService movieService) {
        this.userService = userService;
        this.movieService = movieService;
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        // Redirect if user is already authenticated
        if (isAuthenticated()) {
            return "redirect:/"; // Redirect to homepage or dashboard
        }
        if (error != null) {
            model.addAttribute("error", "Invalid username or password.");
        }
        return "login";
    }


    @GetMapping("/register")
    public String register(Model model) {
        // Redirect if user is already authenticated
        if (isAuthenticated()) {
            return "redirect:/"; // Redirect to homepage or dashboard
        }
        String firstHorrorMoviePoster = movieService.getFirstHorrorMoviePoster();
        model.addAttribute("posterUrl", firstHorrorMoviePoster);
        return "register";
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

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
    }

}

