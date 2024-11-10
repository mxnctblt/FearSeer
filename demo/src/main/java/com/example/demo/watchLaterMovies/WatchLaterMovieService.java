package com.example.demo.watchLaterMovies;

import com.example.demo.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WatchLaterMovieService {

    private final WatchLaterMovieRepository watchLaterMovieRepository;

    @Autowired
    public WatchLaterMovieService(WatchLaterMovieRepository watchLaterMovieRepository) {
        this.watchLaterMovieRepository = watchLaterMovieRepository;
    }

    public void addWatchLaterMovie(User user, String movieTitle, String moviePosterPath, Long movieID) {
        WatchLaterMovie watchLaterMovie = new WatchLaterMovie(user, movieTitle, moviePosterPath, movieID);
        watchLaterMovieRepository.save(watchLaterMovie);
    }

    public void removeWatchLaterMovie(User user, Long movieID) {
        WatchLaterMovie watchLaterMovie = watchLaterMovieRepository.findByUserAndMovieID(user, movieID);
        if (watchLaterMovie != null) {
            watchLaterMovieRepository.delete(watchLaterMovie);
        }
    }

    public List<WatchLaterMovie> getWatchLaterMovies(User user) {
        return watchLaterMovieRepository.findByUser(user);
    }

    public void deleteWatchLaterMoviesByUser(User user) {
        List<WatchLaterMovie> watchLaterMovies = watchLaterMovieRepository.findByUser(user);
        watchLaterMovieRepository.deleteAll(watchLaterMovies);
    }
}
