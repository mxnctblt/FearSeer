package com.example.demo.movies;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

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

    public Map<String, Object> getHorrorMovies() {
        String url = tmdbApiUrl + "/discover/movie?api_key=" + apiKey + "&with_genres=27";
        return restTemplate.getForObject(url, Map.class);
    }
}