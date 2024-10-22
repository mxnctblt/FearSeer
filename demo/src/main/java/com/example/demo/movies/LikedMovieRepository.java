package com.example.demo.movies;

import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikedMovieRepository extends JpaRepository<LikedMovie, Long> {
    List<LikedMovie> findByUser(User user);
    LikedMovie findByUserAndMovieID(User user, Long movieID);
}
