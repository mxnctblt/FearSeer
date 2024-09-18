package com.example.demo.movies;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

    public MovieService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
}