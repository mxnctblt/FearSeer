package com.example.demo.quiz;

import com.example.demo.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
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
    private User user;

    private String movieTitle;
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
    private int score;

}
