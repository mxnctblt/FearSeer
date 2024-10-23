package com.example.demo.watchLaterMovies;

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
@RequestMapping("/watch-later-movies")
public class WatchLaterMovieController {

    private final WatchLaterMovieService watchLaterMovieService;
    private final UserService userService;

    @Autowired
    public WatchLaterMovieController(WatchLaterMovieService watchLaterMovieService, UserService userService) {
        this.watchLaterMovieService = watchLaterMovieService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public String addWatchLaterMovie(@RequestParam String movieTitle,
                                     @RequestParam String moviePosterPath,
                                     @RequestParam Long movieID,
                                     HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        watchLaterMovieService.addWatchLaterMovie(user, movieTitle, moviePosterPath, movieID);

        // Redirect back to the previous page
        String referer = request.getHeader("Referer").replaceAll("^(http[s]?://[^/]+)", "");
        return "redirect:" + referer;
    }

    @PostMapping("/remove")
    public String removeWatchLaterMovie(@RequestParam Long movieID, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        watchLaterMovieService.removeWatchLaterMovie(user, movieID);

        String referer = request.getHeader("Referer").replaceAll("^(http[s]?://[^/]+)", "");
        return "redirect:" + referer;
    }

    @GetMapping
    public List<WatchLaterMovie> getWatchLaterMovies() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        return watchLaterMovieService.getWatchLaterMovies(user);
    }
}