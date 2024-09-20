package com.example.demo.movies;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // Display list of horror movies at root URL
    @GetMapping("/")
    public String home(Model model) {
        return getHorrorMovies(model); // Redirect to the horror movies list
    }

    // Display list of horror movies
    @GetMapping("/horror-movies")
    public String getHorrorMovies(Model model) {
        Map<String, Object> horrorMovies = movieService.getHorrorMovies();
        model.addAttribute("movies", horrorMovies.get("results"));
        return "horror-movies";
    }

    // Search for Horror Movies by title
    @GetMapping("/search")
    public String SearchMovies(@RequestParam("query") String query, Model model) {
        Map<String, Object> searchResults = movieService.searchHorrorMovies(query);
        model.addAttribute("movies", searchResults.get("results"));
        return "horror-movies";
    }
}
