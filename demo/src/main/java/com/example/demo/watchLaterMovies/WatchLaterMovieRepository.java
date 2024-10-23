package com.example.demo.watchLaterMovies;

import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchLaterMovieRepository extends JpaRepository<WatchLaterMovie, Long> {
    List<WatchLaterMovie> findByUser(User user);
    WatchLaterMovie findByUserAndMovieID(User user, Long movieID);
}