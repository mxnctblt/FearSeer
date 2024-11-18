package com.example.demo;

import com.example.demo.movies.MovieService;
import com.example.demo.registration.RegistrationRequest;
import com.example.demo.user.User;
import com.example.demo.user.UserRole;
import com.example.demo.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private MovieService movieService;

    @Test
    void loginPageAccessibleWithoutAuthentication() throws Exception {
        // Test that the login page is accessible without authentication.
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void registerPageAccessibleWithoutAuthentication() throws Exception {
        // Test that the register page is accessible without authentication and contains the poster URL.
        String mockPoster = "http://example.com/mock-poster.jpg";
        when(movieService.getFirstHorrorMoviePoster()).thenReturn(mockPoster);

        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("posterUrl", mockPoster));
    }

    @Test
    @WithMockUser // Mock an authenticated user
    void redirectAuthenticatedUserToHomeOnLoginPage() throws Exception {
        // Test that an authenticated user is redirected to the home page when accessing the login page.
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithMockUser // Mock an authenticated user
    void redirectAuthenticatedUserToHomeOnRegisterPage() throws Exception {
        // Test that an authenticated user is redirected to the home page when accessing the register page.
        mockMvc.perform(get("/auth/register"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void registerUserSuccess() throws Exception {
        // Test successful user registration with valid data.
        RegistrationRequest request = new RegistrationRequest(
                "John", "Doe", "johndoe", "john@example.com", "Password1!"
        );

        when(userService.signUpUser(any(User.class))).thenReturn("User registered successfully");

        mockMvc.perform(post("/auth/register")
                        .flashAttr("request", request)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"))
                .andExpect(flash().attribute("success", "Registration successful! Please log in."));
    }

    @Test
    void registerUserEmailAlreadyTaken() throws Exception {
        // Test registration failure when the email is already taken.
        doThrow(new IllegalStateException("Email already taken"))
                .when(userService).signUpUser(any(User.class));

        RegistrationRequest request = new RegistrationRequest(
                "John", "Doe", "johndoe", "existing@example.com", "Password1!"
        );

        mockMvc.perform(post("/auth/register")
                        .flashAttr("request", request)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register"))
                .andExpect(flash().attribute("error", "Registration failed: Email already taken"));
    }

    @Test
    void registerUserUsernameAlreadyTaken() throws Exception {
        // Test registration failure when the username is already taken.
        doThrow(new IllegalStateException("Username already taken"))
                .when(userService).signUpUser(any(User.class));

        RegistrationRequest request = new RegistrationRequest(
                "John", "Doe", "existingUsername", "john@example.com", "Password1!"
        );

        mockMvc.perform(post("/auth/register")
                        .flashAttr("request", request)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register"))
                .andExpect(flash().attribute("error", "Registration failed: Username already taken"));
    }

    @Test
    void registerUserInvalidEmail() throws Exception {
        // Test registration failure when the email is invalid.
        doThrow(new IllegalStateException("Invalid email address"))
                .when(userService).signUpUser(any(User.class));

        RegistrationRequest request = new RegistrationRequest(
                "Jane", "Doe", "janedoe", "invalidEmail", "Password1!"
        );

        mockMvc.perform(post("/auth/register")
                        .flashAttr("request", request)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register"))
                .andExpect(flash().attribute("error", "Registration failed: Invalid email address"));
    }

    @Test
    void registerUserPasswordTooShort() throws Exception {
        // Test registration failure when the password is too short.
        doThrow(new IllegalStateException("Password must be at least 8 characters long."))
                .when(userService).signUpUser(any(User.class));

        RegistrationRequest request = new RegistrationRequest(
                "Jane", "Doe", "janedoe", "jane@example.com", "Short1"
        );

        mockMvc.perform(post("/auth/register")
                        .flashAttr("request", request)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register"))
                .andExpect(flash().attribute("error", "Registration failed: Password must be at least 8 characters long."));
    }
    @Test
    void registerUserPasswordMissingUppercase() throws Exception {
        // Test registration failure when the password lacks an uppercase letter.
        doThrow(new IllegalStateException("Password must contain at least one uppercase letter."))
                .when(userService).signUpUser(any(User.class));

        RegistrationRequest request = new RegistrationRequest(
                "Jane", "Doe", "janedoe", "jane@example.com", "password1!"
        );

        mockMvc.perform(post("/auth/register")
                        .flashAttr("request", request)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register"))
                .andExpect(flash().attribute("error", "Registration failed: Password must contain at least one uppercase letter."));
    }

    @Test
    void registerUserPasswordMissingLowercase() throws Exception {
        // Test registration failure when the password lacks a lowercase letter.
        doThrow(new IllegalStateException("Password must contain at least one lowercase letter."))
                .when(userService).signUpUser(any(User.class));

        RegistrationRequest request = new RegistrationRequest(
                "Jane", "Doe", "janedoe", "jane@example.com", "PASSWORD1!"
        );

        mockMvc.perform(post("/auth/register")
                        .flashAttr("request", request)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register"))
                .andExpect(flash().attribute("error", "Registration failed: Password must contain at least one lowercase letter."));
    }

    @Test
    void registerUserPasswordMissingNumber() throws Exception {
        // Test registration failure when the password lacks a number.
        doThrow(new IllegalStateException("Password must contain at least one number."))
                .when(userService).signUpUser(any(User.class));

        RegistrationRequest request = new RegistrationRequest(
                "Jane", "Doe", "janedoe", "jane@example.com", "Password!"
        );

        mockMvc.perform(post("/auth/register")
                        .flashAttr("request", request)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register"))
                .andExpect(flash().attribute("error", "Registration failed: Password must contain at least one number."));
    }

    @Test
    void registerUserPasswordTooWeak() throws Exception {
        // Test registration failure when the password is too weak.
        doThrow(new IllegalStateException("Password must contain at least one special character."))
                .when(userService).signUpUser(any(User.class));

        RegistrationRequest request = new RegistrationRequest(
                "Jane", "Doe", "janedoe", "jane@example.com", "WeakPass1"
        );

        mockMvc.perform(post("/auth/register")
                        .flashAttr("request", request)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register"))
                .andExpect(flash().attribute("error", "Registration failed: Password must contain at least one special character."));
    }

    @Test
    void loginWithInvalidCredentials() throws Exception {
        // Test login failure with invalid credentials.
        mockMvc.perform(post("/auth/login")
                        .param("username", "wrong@example.com")
                        .param("password", "wrongpass")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?error"));
    }

    @Test
    void loginWithValidCredentials() throws Exception {
        // Test successful login with valid credentials.
        User mockUser = new User("John", "Doe", "johndoe", "john@example.com", "Password1!", UserRole.USER);
        when(userService.loadUserByUsername("john@example.com")).thenReturn(mockUser);

        // Mock successful authentication
        mockUser.setPassword(new BCryptPasswordEncoder().encode("Password1!"));

        mockMvc.perform(post("/auth/login")
                        .param("username", "john@example.com")
                        .param("password", "Password1!")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

}
