package com.example.demo.seenMovies;

import com.example.demo.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "seen_movies")
public class SeenMovie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    private String movieTitle;
    private String moviePosterPath;
    private Long movieID;

    public SeenMovie() {}

    public SeenMovie(User user, String movieTitle, String moviePosterPath, Long movieID) {
        this.user = user;
        this.movieTitle = movieTitle;
        this.moviePosterPath = moviePosterPath;
        this.movieID = movieID;
    }
}