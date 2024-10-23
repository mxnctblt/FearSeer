package com.example.demo.movies;

import com.example.demo.user.User;
import com.example.demo.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/liked-movies")
public class LikedMovieController {

    private final LikedMovieService likedMovieService;
    private final UserService userService;

    @Autowired
    public LikedMovieController(LikedMovieService likedMovieService, UserService userService) {
        this.likedMovieService = likedMovieService;
        this.userService = userService;
    }

    @PostMapping("/like")
    public String likeMovie(@RequestParam String movieTitle,
                            @RequestParam String moviePosterPath,
                            @RequestParam Long movieID,
                            HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        likedMovieService.likeMovie(user, movieTitle, moviePosterPath, movieID);

        // Redirect back to the previous page
        String referer = request.getHeader("Referer");
        // Extract the path (removing localhost:8080 or domain part)
        if (referer != null) {
            // Remove the "http://localhost:8080" part, assuming this format
            referer = referer.replaceAll("^(http[s]?://[^/]+)", "");
        }
        return "redirect:" + referer;
    }

    @PostMapping("/unlike")
    public String unlikeMovie(@RequestParam Long movieID, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        likedMovieService.unlikeMovie(user, movieID);

        // Redirect back to the previous page
        String referer = request.getHeader("Referer");
        // Extract the path (removing localhost:8080 or domain part)
        if (referer != null) {
            // Remove the "http://localhost:8080" part, assuming this format
            referer = referer.replaceAll("^(http[s]?://[^/]+)", "");
        }
        return "redirect:" + referer;
    }

    @GetMapping
    public List<LikedMovie> getLikedMovies() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        return likedMovieService.getLikedMovies(user);
    }
}
