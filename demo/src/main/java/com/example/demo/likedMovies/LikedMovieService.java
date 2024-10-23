package com.example.demo.likedMovies;

import com.example.demo.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikedMovieService {

    private final LikedMovieRepository likedMovieRepository;

    @Autowired
    public LikedMovieService(LikedMovieRepository likedMovieRepository) {
        this.likedMovieRepository = likedMovieRepository;
    }

    public void likeMovie(User user, String movieTitle, String moviePosterPath, Long movieID) {
        LikedMovie likedMovie = new LikedMovie(user, movieTitle, moviePosterPath, movieID);
        likedMovieRepository.save(likedMovie);
    }

    public void unlikeMovie(User user, Long movieID) {
        LikedMovie likedMovie = likedMovieRepository.findByUserAndMovieID(user, movieID);
        if (likedMovie != null) {
            likedMovieRepository.delete(likedMovie);
        }
    }

    public List<LikedMovie> getLikedMovies(User user) {
        return likedMovieRepository.findByUser(user);
    }
}
