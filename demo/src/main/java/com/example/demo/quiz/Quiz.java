package com.example.demo.quiz;

import com.example.demo.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "quiz")
public class Quiz {

    @SequenceGenerator(
            name = "quiz_sequence",
            sequenceName = "quiz_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "quiz_sequence"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;  // Link the quiz to a user

    private String movieTitle; // The movie this quiz is about
    private int deathCountPrediction;
    private String firstDeathPrediction;
    private String finalSurvivorPrediction;
    private int jumpScarePrediction;
    private String weaponPrediction;
    private String catchphrasePrediction;
    private String romancePrediction;
    private int score;

}
