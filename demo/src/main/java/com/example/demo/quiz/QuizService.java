package com.example.demo.quiz;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class QuizService {

    private final ResourceLoader resourceLoader;
    private List<Map<String, Object>> movieAnswers;

    @Autowired
    public QuizService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void loadQuizAnswers() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Resource resource = resourceLoader.getResource("classpath:movies_answers.json");
        movieAnswers = mapper.readValue(
                resource.getInputStream(),
                new TypeReference<>() {}
        );
    }

    public Map<String, Object> getAnswersForMovie(String title) {
        return movieAnswers.stream()
                .filter(movie -> movie.get("movieTitle").equals(title))
                .findFirst()
                .orElse(null);
    }

    public int calculateScore(Map<String, Object> userAnswers, Map<String, Object> correctAnswers) {
        int score = 0;

        // compare the user's answers with the correct ones
        if (userAnswers.get("deathCount").equals(correctAnswers.get("deathCount"))) score++;
        if (userAnswers.get("lifeCount").equals(correctAnswers.get("lifeCount"))) score++;
        if (userAnswers.get("mainCharacter").equals(correctAnswers.get("mainCharacter"))) score++;
        if (userAnswers.get("jumpScares").equals(correctAnswers.get("jumpScares"))) score++;
        if (userAnswers.get("romance").equals(correctAnswers.get("romance"))) score++;
        if (userAnswers.get("blood").equals(correctAnswers.get("blood"))) score++;
        if (userAnswers.get("finalGirl").equals(correctAnswers.get("finalGirl"))) score++;
        if (userAnswers.get("creaking").equals(correctAnswers.get("creaking"))) score++;
        if (userAnswers.get("weapon").equals(correctAnswers.get("weapon"))) score++;
        if (userAnswers.get("killer").equals(correctAnswers.get("killer"))) score++;

        return score;
    }
}
