package com.example.demo.seenMovies;

import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeenMovieRepository extends JpaRepository<SeenMovie, Long> {
    List<SeenMovie> findByUser(User user);
    SeenMovie findByUserAndMovieID(User user, Long movieID);
}