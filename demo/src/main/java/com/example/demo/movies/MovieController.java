package com.example.demo.movies;

import com.example.demo.likedMovies.LikedMovie;
import com.example.demo.likedMovies.LikedMovieService;
import com.example.demo.seenMovies.SeenMovie;
import com.example.demo.seenMovies.SeenMovieService;
import com.example.demo.user.User;
import com.example.demo.watchLaterMovies.WatchLaterMovie;
import com.example.demo.watchLaterMovies.WatchLaterMovieService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MovieController {
    private final MovieService movieService;
    private final LikedMovieService likedMovieService;
    private final WatchLaterMovieService watchLaterMovieService;
    private final SeenMovieService seenMovieService;

    public MovieController(MovieService movieService,
                           LikedMovieService likedMovieService,
                           WatchLaterMovieService watchLaterMovieService,
                           SeenMovieService seenMovieService) {
        this.movieService = movieService;
        this.likedMovieService = likedMovieService;
        this.watchLaterMovieService = watchLaterMovieService;
        this.seenMovieService = seenMovieService;
    }

    // Display list of horror movies at root URL
    @GetMapping("/")
    public String getHorrorMovies(Model model) {
        Map<String, Object> horrorMovies = movieService.getHorrorMovies();
        List<String> quizMovieTitles = movieService.getQuizMovieTitles();
        model.addAttribute("movies", horrorMovies.get("results"));
        model.addAttribute("quizMovieTitles", quizMovieTitles);

        // Get authenticated user details, if available
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            model.addAttribute("user", user);

            // Fetch liked, watch later, and seen movie IDs for the current user
            List<LikedMovie> likedMovies = likedMovieService.getLikedMovies(user);
            List<Integer> likedMovieIds = likedMovies.stream()
                    .map(likedMovie -> likedMovie.getMovieID().intValue())
                    .collect(Collectors.toList());
            model.addAttribute("likedMoviesByUser", likedMovieIds);

            List<WatchLaterMovie> watchLaterMovies = watchLaterMovieService.getWatchLaterMovies(user);
            List<Integer> watchLaterMovieIds = watchLaterMovies.stream()
                    .map(watchLaterMovie -> watchLaterMovie.getMovieID().intValue())
                    .collect(Collectors.toList());
            model.addAttribute("watchLaterMoviesByUser", watchLaterMovieIds);

            List<SeenMovie> seenMovies = seenMovieService.getSeenMovies(user);
            List<Integer> seenMovieIds = seenMovies.stream()
                    .map(seenMovie -> seenMovie.getMovieID().intValue())
                    .collect(Collectors.toList());
            model.addAttribute("seenMoviesByUser", seenMovieIds);

        }

        return "horror-movies";
    }

    // Search for Horror Movies by title
    @GetMapping("/search")
    public String SearchMovies(@RequestParam("query") String query, Model model) {
        Map<String, Object> searchResults = movieService.searchHorrorMovies(query);
        List<String> quizMovieTitles = movieService.getQuizMovieTitles();
        model.addAttribute("movies", searchResults.get("results"));
        model.addAttribute("quizMovieTitles", quizMovieTitles);

        // Get authenticated user details, if available
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            model.addAttribute("user", user);

            // Fetch liked, watch later, and seen movies IDs for the current user
            List<LikedMovie> likedMovies = likedMovieService.getLikedMovies(user);
            List<Integer> likedMovieIds = likedMovies.stream()
                    .map(likedMovie -> likedMovie.getMovieID().intValue())
                    .collect(Collectors.toList());
            model.addAttribute("likedMoviesByUser", likedMovieIds);

            List<WatchLaterMovie> watchLaterMovies = watchLaterMovieService.getWatchLaterMovies(user);
            List<Integer> watchLaterMovieIds = watchLaterMovies.stream()
                    .map(watchLaterMovie -> watchLaterMovie.getMovieID().intValue())
                    .collect(Collectors.toList());
            model.addAttribute("watchLaterMoviesByUser", watchLaterMovieIds);

            List<SeenMovie> seenMovies = seenMovieService.getSeenMovies(user);
            List<Integer> seenMovieIds = seenMovies.stream()
                    .map(seenMovie -> seenMovie.getMovieID().intValue())
                    .collect(Collectors.toList());
            model.addAttribute("seenMoviesByUser", seenMovieIds);

        }
        return "horror-movies";
    }

    // Display only horror movies with quizzes
    @GetMapping("/horror-movies-with-quiz")
    public String getHorrorMoviesWithQuiz(Model model) {
        List<Map<String, Object>> moviesWithQuiz = movieService.getMoviesWithQuiz();

        model.addAttribute("movies", moviesWithQuiz);

        // Get authenticated user details, if available
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();
            model.addAttribute("user", user);
        }
        return "horror-movies-with-quiz";
    }
}
