package com.example.demo.watchLaterMovies;

import com.example.demo.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "watch_later_movies")
public class WatchLaterMovie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    private String movieTitle;
    private String moviePosterPath;
    private Long movieID;

    public WatchLaterMovie() {}

    public WatchLaterMovie(User user, String movieTitle, String moviePosterPath, Long movieID) {
        this.user = user;
        this.movieTitle = movieTitle;
        this.moviePosterPath = moviePosterPath;
        this.movieID = movieID;
    }
}
