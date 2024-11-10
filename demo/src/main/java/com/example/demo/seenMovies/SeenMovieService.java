package com.example.demo.seenMovies;

import com.example.demo.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeenMovieService {

    private final SeenMovieRepository seenMovieRepository;

    @Autowired
    public SeenMovieService(SeenMovieRepository seenMovieRepository) {
        this.seenMovieRepository = seenMovieRepository;
    }

    public void addSeenMovie(User user, String movieTitle, String moviePosterPath, Long movieID) {
        SeenMovie seenMovie = new SeenMovie(user, movieTitle, moviePosterPath, movieID);
        seenMovieRepository.save(seenMovie);
    }

    public void removeSeenMovie(User user, Long movieID) {
        SeenMovie seenMovie = seenMovieRepository.findByUserAndMovieID(user, movieID);
        if (seenMovie != null) {
            seenMovieRepository.delete(seenMovie);
        }
    }

    public List<SeenMovie> getSeenMovies(User user) {
        return seenMovieRepository.findByUser(user);
    }

    public void deleteSeenMoviesByUser(User user) {
        List<SeenMovie> seenMovies = seenMovieRepository.findByUser(user);
        seenMovieRepository.deleteAll(seenMovies);
    }

}
