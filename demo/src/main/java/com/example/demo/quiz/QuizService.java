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

    // Autowire the ResourceLoader to load resources from the classpath
    @Autowired
    public QuizService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    // This method is automatically called after the service is initialized
    @PostConstruct
    public void loadQuizAnswers() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // Use ResourceLoader to load the resource from the classpath
        Resource resource = resourceLoader.getResource("classpath:movies_answers.json");

        // Read the JSON data from the resource's input stream and store it in movieAnswers
        movieAnswers = mapper.readValue(
                resource.getInputStream(),
                new TypeReference<>() {}  // Simplified type reference as per the warning
        );
    }

    public Map<String, Object> getAnswersForMovie(String title) {
        return movieAnswers.stream()
                .filter(movie -> movie.get("movieTitle").equals(title))
                .findFirst()
                .orElse(null); // Return null if the movie isn't found
    }

    public int calculateScore(Map<String, Object> userAnswers, Map<String, Object> correctAnswers) {
        int score = 0;

        if (userAnswers.get("deathCount").equals(correctAnswers.get("deathCount"))) score++;
        if (userAnswers.get("firstDeath").equals(correctAnswers.get("firstDeath"))) score++;
        if (userAnswers.get("finalSurvivor").equals(correctAnswers.get("finalSurvivor"))) score++;
        if (userAnswers.get("jumpScares").equals(correctAnswers.get("jumpScares"))) score++;
        if (userAnswers.get("weapon").equals(correctAnswers.get("weapon"))) score++;
        if (userAnswers.get("catchphrase").equals(correctAnswers.get("catchphrase"))) score++;
        if (userAnswers.get("romance").equals(correctAnswers.get("romance"))) score++;

        return score; // 7 is the max score
    }
}
