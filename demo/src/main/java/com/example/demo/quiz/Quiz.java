package com.example.demo.quiz;

import com.example.demo.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to a specific User
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    // Movie Title for which the user is taking a quiz
    private String movieTitle;

    // Predictions on the movie
    private int deathCountPrediction;
    private int lifeCountPrediction;
    private String mainCharacterPrediction;
    private int jumpScarePrediction;
    private String romancePrediction;
    private String bloodPrediction;
    private String finalGirlPrediction;
    private String creakingPrediction;
    private String weaponPrediction;
    private String killerPrediction;

    // Score obtained by the user
    private int score;

}
