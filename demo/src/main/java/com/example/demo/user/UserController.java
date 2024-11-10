package com.example.demo.user;

import com.example.demo.likedMovies.LikedMovie;
import com.example.demo.likedMovies.LikedMovieService;
import com.example.demo.seenMovies.SeenMovie;
import com.example.demo.seenMovies.SeenMovieService;
import com.example.demo.watchLaterMovies.WatchLaterMovie;
import com.example.demo.watchLaterMovies.WatchLaterMovieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
    private final WatchLaterMovieService watchLaterMovieService;
    private final SeenMovieService seenMovieService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(LikedMovieService likedMovieService,
                          WatchLaterMovieService watchLaterMovieService,
                          SeenMovieService seenMovieService,
                          UserService userService) {
        this.likedMovieService = likedMovieService;
        this.watchLaterMovieService = watchLaterMovieService;
        this.seenMovieService = seenMovieService;
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

        // Fetch Watch Later movies
        List<WatchLaterMovie> watchLaterMovies = watchLaterMovieService.getWatchLaterMovies(user);
        model.addAttribute("watchLaterMovies", watchLaterMovies);

        // Fetch Seen movies
        List<SeenMovie> seenMovies = seenMovieService.getSeenMovies(user);
        model.addAttribute("seenMovies", seenMovies);

        // Convert profile picture to Base64 for display in the HTML
        if (user.getProfilePicture() != null) {
            String profilePictureBase64 = Base64.getEncoder().encodeToString(user.getProfilePicture());
            model.addAttribute("profilePictureBase64", profilePictureBase64);
        }

        return "profile";
    }
    @GetMapping("/profile/{userUsername}/settings")
    public String showProfileSettings(@PathVariable String userUsername, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        if (!user.getUserUsername().equals(userUsername)) {
            return "redirect:/";
        }

        model.addAttribute("user", user);

        // Convert profile picture to Base64 for display in the HTML
        if (user.getProfilePicture() != null) {
            String profilePictureBase64 = Base64.getEncoder().encodeToString(user.getProfilePicture());
            model.addAttribute("profilePictureBase64", profilePictureBase64);
        }
        return "profile-settings";
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

    @PostMapping("/profile/{userUsername}/delete")
    public String deleteUser(@PathVariable String userUsername, RedirectAttributes redirectAttributes,
                             HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        if (!user.getUserUsername().equals(userUsername)) {
            return "redirect:/";
        }

        try {
            userService.deleteUser(user);
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, auth);
            redirectAttributes.addFlashAttribute("success", "User account deleted successfully.");
        } catch (Exception e) {
            logger.error("User deletion failed: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to delete user: " + e.getMessage());
        }

        return "redirect:/";
    }
}
