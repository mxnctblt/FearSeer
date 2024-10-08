package com.example.demo.movies;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // Display list of horror movies at root URL
    @GetMapping("/")
    public String getHorrorMovies(Model model) {
        Map<String, Object> horrorMovies = movieService.getHorrorMovies();
        List<String> quizMovieTitles = movieService.getQuizMovieTitles();
        model.addAttribute("movies", horrorMovies.get("results"));
        model.addAttribute("quizMovieTitles", quizMovieTitles);
        return "horror-movies";
    }

    // Search for Horror Movies by title
    @GetMapping("/search")
    public String SearchMovies(@RequestParam("query") String query, Model model) {
        Map<String, Object> searchResults = movieService.searchHorrorMovies(query);
        List<String> quizMovieTitles = movieService.getQuizMovieTitles();
        model.addAttribute("movies", searchResults.get("results"));
        model.addAttribute("quizMovieTitles", quizMovieTitles);
        return "horror-movies";
    }

    // Display only horror movies with quizzes
    @GetMapping("/horror-movies-with-quiz")
    public String getHorrorMoviesWithQuiz(Model model) {
        List<Map<String, Object>> moviesWithQuiz = movieService.getMoviesWithQuiz();

        model.addAttribute("movies", moviesWithQuiz);
        return "horror-movies-with-quiz";
    }
}
