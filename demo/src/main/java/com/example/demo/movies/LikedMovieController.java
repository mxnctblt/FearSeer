package com.example.demo.movies;

import com.example.demo.user.User;
import com.example.demo.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
    public void likeMovie(@RequestParam String movieTitle,
                          @RequestParam String moviePosterPath,
                          @RequestParam Long movieID) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        likedMovieService.likeMovie(user, movieTitle, moviePosterPath, movieID);
    }

    @GetMapping
    public List<LikedMovie> getLikedMovies() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        return likedMovieService.getLikedMovies(user);
    }
}
