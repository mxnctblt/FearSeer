package com.example.demo;

import com.example.demo.quiz.Quiz;
import com.example.demo.quiz.QuizRepository;
import com.example.demo.quiz.QuizService;
import com.example.demo.user.User;
import com.example.demo.user.UserRole;
import com.example.demo.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class QuizTest {

    @MockBean
    private QuizService quizService;

    @MockBean
    private QuizRepository quizRepository;


    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "john@example.com", roles = "USER")
    void showQuizForAuthenticatedUser() throws Exception {
        // Mock the service response for the movie quiz answers
        String movieTitle = "The Conjuring";
        Map<String, Object> correctAnswers = Map.of(
                "deathCount", 3,
                "lifeCount", 2,
                "mainCharacter", "Yes",
                "jumpScares", 12,
                "romance", "Yes",
                "blood", "No",
                "finalGirl", "No",
                "creaking", "Yes",
                "weapon", "Supernatural Force",
                "killer", "Demon"
        );
        when(quizService.getAnswersForMovie(movieTitle)).thenReturn(correctAnswers);

        // Mock user explicitly
        User mockUser = new User("John", "Doe", "johndoe", "john@example.com", "Password1!", UserRole.USER);

        // Perform GET request to show the quiz form
        mockMvc.perform(get("/quiz/{movieTitle}", movieTitle)
                        .flashAttr("user", mockUser)  // Add user to the model
                        .with(csrf()))  // Ensure CSRF token is included
                .andExpect(status().isOk())
                .andExpect(view().name("quiz-form"))
                .andExpect(model().attributeExists("movieTitle"))
                .andExpect(model().attribute("movieTitle", movieTitle))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", mockUser));
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "USER")
    void submitQuizForAuthenticatedUser() throws Exception {
        // Mock the service response for the movie quiz answers
        String movieTitle = "The Conjuring";
        Map<String, Object> correctAnswers = Map.of(
                "deathCount", 3,
                "lifeCount", 2,
                "mainCharacter", "Yes",
                "jumpScares", 12,
                "romance", "Yes",
                "blood", "No",
                "finalGirl", "No",
                "creaking", "Yes",
                "weapon", "Supernatural Force",
                "killer", "Demon"
        );
        when(quizService.getAnswersForMovie(movieTitle)).thenReturn(correctAnswers);

        // Prepare the user's answers
        Map<String, String> userAnswers = Map.of(
                "deathCount", "3",
                "lifeCount", "2",
                "mainCharacter", "Yes",
                "jumpScares", "12",
                "romance", "Yes",
                "blood", "No",
                "finalGirl", "No",
                "creaking", "Yes",
                "weapon", "Supernatural Force",
                "killer", "Demon"
        );

        // Convert the user answers into a MultiValueMap
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        userAnswers.forEach((key, value) -> params.add(key, value));

        // Mock user and mock the UserService to return the mock user
        User mockUser = new User("John", "Doe", "johndoe", "john@example.com", "Password1!", UserRole.USER);
        when(userService.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));

        // Mock the quiz repository to return a quiz with a known ID
        Quiz savedQuiz = new Quiz();
        savedQuiz.setId(1L); // Use a fixed ID for the test
        when(quizRepository.save(any(Quiz.class))).thenReturn(savedQuiz);

        // Perform POST request to submit the quiz
        mockMvc.perform(post("/quiz/{movieTitle}", movieTitle)
                        .params(params)  // Add the converted MultiValueMap as params
                        .flashAttr("user", mockUser)  // Add user to the model
                        .with(csrf()))  // Ensure CSRF token is included
                .andExpect(status().is3xxRedirection())  // Expect a redirect
                .andExpect(redirectedUrlPattern("/quiz/{movieTitle}/quiz-result/*"));
    }

    @Test
    @WithMockUser(username = "john@example.com", roles = "USER")
    void showQuizResultForAuthenticatedUser() throws Exception {
        // Mock the user
        User mockUser = new User("John", "Doe", "johndoe", "john@example.com", "Password1!", UserRole.USER);

        // Mock the quiz data
        String movieTitle = "The Conjuring";
        Long quizId = 1L;
        Quiz mockQuiz = new Quiz();
        mockQuiz.setId(quizId);
        mockQuiz.setMovieTitle(movieTitle);
        mockQuiz.setUser(mockUser);
        mockQuiz.setScore(8);  // Example score

        // Mock the correct answers for the movie
        Map<String, Object> correctAnswers = Map.of(
                "deathCount", 3,
                "lifeCount", 2,
                "mainCharacter", "Yes",
                "jumpScares", 12,
                "romance", "Yes",
                "blood", "No",
                "finalGirl", "No",
                "creaking", "Yes",
                "weapon", "Supernatural Force",
                "killer", "Demon"
        );
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(mockQuiz));
        when(quizService.getAnswersForMovie(movieTitle)).thenReturn(correctAnswers);

        // Mock the userService to return the mock user
        when(userService.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));

        // Perform the GET request to show the quiz result
        mockMvc.perform(get("/quiz/{movieTitle}/quiz-result/{quizId}", movieTitle, quizId)
                        .with(csrf()))  // Ensure CSRF token is included
                .andExpect(status().isOk())  // Expect the status to be OK
                .andExpect(view().name("quiz-result"))  // Expect the quiz-result view
                .andExpect(model().attributeExists("quiz"))  // Expect the quiz object in the model
                .andExpect(model().attribute("quiz", mockQuiz))  // Expect the quiz object to match mockQuiz
                .andExpect(model().attributeExists("correctAnswers"))  // Expect correct answers to be in the model
                .andExpect(model().attribute("correctAnswers", correctAnswers))  // Check correct answers match
                .andExpect(model().attributeExists("user"))  // Expect the user object in the model
                .andExpect(model().attribute("user", mockUser));  // Ensure the user object is the mock user
    }
}
