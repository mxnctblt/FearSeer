package com.example.demo.user;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MESSAGE = "User with email %s not found";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, email)));
    }
    public String signUpUser(User user) {
        // Check if email already exists
        boolean emailExists = userRepository.findByEmail(user.getEmail()).isPresent();
        if (emailExists) {
            throw new IllegalStateException("Email already taken");
        }

        // Check if username already exists
        boolean usernameExists = userRepository.findByUsername(user.getUserUsername()).isPresent();
        if (usernameExists) {
            throw new IllegalStateException("Username already taken");
        }

        // Check password strength
        String passwordError = validatePassword(user.getPassword());
        if (passwordError != null) {
            throw new IllegalStateException(passwordError);
        }

        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        return "User registered successfully";
    }

    public void updateUserProfile(User user, String bio, MultipartFile profilePicture) throws Exception {
        // Update bio if provided
        user.setBio(bio);

        // Define a maximum size for the profile picture (e.g., 2 MB)
        long maxSize = 2 * 1024 * 1024; // 2 MB

        // If a new profile picture is uploaded, validate and update it
        if (profilePicture != null && !profilePicture.isEmpty()) {
            // Validate file size
            if (profilePicture.getSize() > maxSize) {
                throw new IllegalStateException("Profile picture must be less than 2 MB.");
            }
            // Convert and set the profile picture bytes
            user.setProfilePicture(profilePicture.getBytes());
        }
        // Save the updated user profile
        userRepository.save(user);
    }

    private String validatePassword(String password) {
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter.";
        }
        if (!password.matches(".*[0-9].*")) {
            return "Password must contain at least one number.";
        }
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return "Password must contain at least one special character.";
        }
        return null;
    }

    public Optional<User> findByEmail(String username) {
        return userRepository.findByEmail(username);
    }

}
