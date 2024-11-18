package com.example.demo;

import com.example.demo.movies.MovieService;
import com.example.demo.user.User;
import com.example.demo.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieTest {

    @MockBean
    private MovieService movieService;

    @Autowired
    private MockMvc mockMvc;

    // Test to simulate login with valid credentials and then access the horror movies page
    @Test
    @WithMockUser(username = "john@example.com", roles = "USER")
    void getHorrorMoviesPageForAuthenticatedUser() throws Exception {
        // Mock the service response for movie list
        when(movieService.getHorrorMovies()).thenReturn(Map.of("results", List.of(
                Map.of("id", 1, "title", "Movie1", "poster_path", "/path1"),
                Map.of("id", 2, "title", "Movie2", "poster_path", "/path2")
        )));
        when(movieService.getQuizMovieIds()).thenReturn(List.of(1, 2));

        // Mock user explicitly
        User mockUser = new User("John", "Doe", "johndoe", "john@example.com", "Password1!", UserRole.USER);

        // Prepare mock data for liked, watch later, and seen movies
        List<Integer> likedMoviesByUser = List.of(1);
        List<Integer> watchLaterMoviesByUser = List.of(2);
        List<Integer> seenMoviesByUser = List.of(1);

        // Perform GET request to access the horror-movies page with explicit attributes
        mockMvc.perform(get("/")
                        .flashAttr("user", mockUser)  // Add user to the model
                        .flashAttr("likedMoviesByUser", likedMoviesByUser)  // Add liked movies
                        .flashAttr("watchLaterMoviesByUser", watchLaterMoviesByUser)  // Add watch later movies
                        .flashAttr("seenMoviesByUser", seenMoviesByUser)  // Add seen movies
                        .with(csrf()))  // Ensure CSRF token is included
                .andExpect(status().isOk())
                .andExpect(view().name("horror-movies"))
                .andExpect(model().attributeExists("movies"))
                .andExpect(model().attributeExists("quizMovieIds"))
                .andExpect(model().attribute("user", mockUser))
                .andExpect(model().attribute("likedMoviesByUser", likedMoviesByUser))
                .andExpect(model().attribute("watchLaterMoviesByUser", watchLaterMoviesByUser))
                .andExpect(model().attribute("seenMoviesByUser", seenMoviesByUser));
    }

    // Test to ensure the page is not accessible for unauthenticated users
    @Test
    void getHorrorMoviesPageForUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/auth/login"));
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "USER")
    void searchHorrorMoviesForAuthenticatedUser() throws Exception {
        // Mock the service response for search results
        when(movieService.searchHorrorMovies("searchQuery")).thenReturn(Map.of("results", List.of(
                Map.of("id", 1, "title", "SearchResult1", "poster_path", "/searchPath1"),
                Map.of("id", 2, "title", "SearchResult2", "poster_path", "/searchPath2")
        )));
        when(movieService.getQuizMovieIds()).thenReturn(List.of(1, 2));

        // Mock user explicitly
        User mockUser = new User("John", "Doe", "johndoe", "john@example.com", "Password1!", UserRole.USER);

        // Prepare mock data for liked, watch later, and seen movies
        List<Integer> likedMoviesByUser = List.of(1);
        List<Integer> watchLaterMoviesByUser = List.of(2);
        List<Integer> seenMoviesByUser = List.of(1);

        // Perform GET request to search endpoint
        mockMvc.perform(get("/search")
                        .param("query", "searchQuery")
                        .flashAttr("user", mockUser)
                        .flashAttr("likedMoviesByUser", likedMoviesByUser)
                        .flashAttr("watchLaterMoviesByUser", watchLaterMoviesByUser)
                        .flashAttr("seenMoviesByUser", seenMoviesByUser)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("horror-movies"))
                .andExpect(model().attributeExists("movies"))
                .andExpect(model().attributeExists("quizMovieIds"))
                .andExpect(model().attribute("user", mockUser))
                .andExpect(model().attribute("likedMoviesByUser", likedMoviesByUser))
                .andExpect(model().attribute("watchLaterMoviesByUser", watchLaterMoviesByUser))
                .andExpect(model().attribute("seenMoviesByUser", seenMoviesByUser));
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "USER")
    void getHorrorMoviesWithQuizForAuthenticatedUser() throws Exception {
        // Mock the service response for movies with quizzes
        when(movieService.getMoviesWithQuiz()).thenReturn(List.of(
                Map.of("id", 1, "title", "QuizMovie1", "poster_path", "/quizPath1"),
                Map.of("id", 2, "title", "QuizMovie2", "poster_path", "/quizPath2")
        ));

        // Mock user explicitly
        User mockUser = new User("John", "Doe", "johndoe", "john@example.com", "Password1!", UserRole.USER);

        // Perform GET request to quiz movies endpoint
        mockMvc.perform(get("/horror-movies-with-quiz")
                        .flashAttr("user", mockUser)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("horror-movies-with-quiz"))
                .andExpect(model().attributeExists("movies"))
                .andExpect(model().attribute("user", mockUser));
    }

    @Test
    void searchHorrorMoviesForUnauthenticatedUser() throws Exception {
        // Perform GET request to search endpoint
        mockMvc.perform(get("/search"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/auth/login"));
    }

    @Test
    void getHorrorMoviesWithQuizForUnauthenticatedUser() throws Exception {
        // Perform GET request to quiz movies endpoint
        mockMvc.perform(get("/horror-movies-with-quiz"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/auth/login"));
    }
}