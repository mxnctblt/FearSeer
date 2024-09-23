package com.example.demo.movies;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.url}")
    private String tmdbApiUrl;

    private final RestTemplate restTemplate;
    private final ResourceLoader resourceLoader;
    private List<Map<String, Object>> quizMovies;


    @Autowired
    public MovieService(RestTemplate restTemplate, ResourceLoader resourceLoader) {
        this.restTemplate = restTemplate;
        this.resourceLoader = resourceLoader;
    }

    // Load available quiz movie titles from JSON
    @PostConstruct
    public void loadQuizMovies() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Resource resource = resourceLoader.getResource("classpath:movies_answers.json");
        quizMovies = mapper.readValue(
                resource.getInputStream(),
                new TypeReference<>() {}
        );
    }

    // Get list of movie titles with available quizzes
    public List<String> getQuizMovieTitles() {
        return quizMovies.stream()
                .map(movie -> (String) movie.get("movieTitle"))
                .collect(Collectors.toList());
    }

    // Get a list of Popular Horror Movies of the moment (Default)
    public Map<String, Object> getHorrorMovies() {
        String url = UriComponentsBuilder.fromHttpUrl(tmdbApiUrl)
                .path("/discover/movie")
                .queryParam("api_key", apiKey)
                .queryParam("with_genres", 27) //27 is genre Id for Horror Movies
                .toUriString();
        return restTemplate.getForObject(url, Map.class);
    }

    // Searcg for Horror Movies by title
    public Map<String, Object> searchHorrorMovies(String query) {
        String url = UriComponentsBuilder.fromHttpUrl(tmdbApiUrl)
                .path("/search/movie")
                .queryParam("api_key", apiKey)
                .queryParam("query", query)
                .toUriString();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        // Filter the results to only include horror genre
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        List<Map<String, Object>> horrorMovies = results.stream()
                .filter(movie -> {
                    List<Integer> genreIds = (List<Integer>) movie.get("genre_ids");
                    return genreIds != null && genreIds.contains(27);
                })
                .collect(Collectors.toList());

        response.put("results", horrorMovies);
        return response;
    }

    // Filter movies to only include those with quizzes available
    public List<Map<String, Object>> getMoviesWithQuiz() {
        return quizMovies;
    }

    public String getFirstHorrorMoviePoster() {
        Map<String, Object> horrorMovies = getHorrorMovies();
        List<Map<String, Object>> results = (List<Map<String, Object>>) horrorMovies.get("results");

        if (!results.isEmpty()) {
            Map<String, Object> firstMovie = results.get(0);
            String posterPath = (String) firstMovie.get("poster_path");
            return "https://image.tmdb.org/t/p/w500" + posterPath;
        }
        return null; // Return null if no movies found
    }
}