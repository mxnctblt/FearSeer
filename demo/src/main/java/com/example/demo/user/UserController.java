package com.example.demo.user;

import com.example.demo.movies.LikedMovie;
import com.example.demo.movies.LikedMovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;
import java.util.List;

@Controller
public class UserController {

    private final LikedMovieService likedMovieService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(LikedMovieService likedMovieService, UserService userService) {
        this.likedMovieService = likedMovieService;
        this.userService = userService;
    }

    @GetMapping("/profile/{userUsername}")
    public String showUserProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        model.addAttribute("user", user);

        // Fetch liked movies
        List<LikedMovie> likedMovies = likedMovieService.getLikedMovies(user);
        model.addAttribute("likedMovies", likedMovies);

        // Convert profile picture to Base64 for display in the HTML
        if (user.getProfilePicture() != null) {
            String profilePictureBase64 = Base64.getEncoder().encodeToString(user.getProfilePicture());
            model.addAttribute("profilePictureBase64", profilePictureBase64);
        }

        return "profile";
    }

    @PostMapping("/profile/{userUsername}/update-profile")
    public String updateUserProfile(@PathVariable String userUsername,
                                    @RequestParam("bio") String bio,
                                    @RequestParam("profilePicture") MultipartFile profilePicture,
                                    RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        // Ensure that the user is updating their own profile
        if (!user.getUserUsername().equals(userUsername)) {
            return "redirect:/"; // Redirect to home if unauthorized access is attempted
        }

        try {
            userService.updateUserProfile(user, bio, profilePicture);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        } catch (IllegalStateException e) {
            logger.error("Profile update failed: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error occurred during profile update", e);
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred. Please try again.");
        }
        return "redirect:/profile/" + user.getUserUsername();
    }
}
