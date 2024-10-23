package com.example.demo.seenMovies;

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
@RequestMapping("/seen-movies")
public class SeenMovieController {

    private final SeenMovieService seenMovieService;
    private final UserService userService;

    @Autowired
    public SeenMovieController(SeenMovieService seenMovieService, UserService userService) {
        this.seenMovieService = seenMovieService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public String addSeenMovie(@RequestParam String movieTitle,
                               @RequestParam String moviePosterPath,
                               @RequestParam Long movieID,
                               HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        seenMovieService.addSeenMovie(user, movieTitle, moviePosterPath, movieID);

        String referer = request.getHeader("Referer").replaceAll("^(http[s]?://[^/]+)", "");
        return "redirect:" + referer;
    }

    @PostMapping("/remove")
    public String removeSeenMovie(@RequestParam Long movieID, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        seenMovieService.removeSeenMovie(user, movieID);

        String referer = request.getHeader("Referer").replaceAll("^(http[s]?://[^/]+)", "");
        return "redirect:" + referer;
    }

    @GetMapping
    public List<SeenMovie> getSeenMovies() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        return seenMovieService.getSeenMovies(user);
    }
}