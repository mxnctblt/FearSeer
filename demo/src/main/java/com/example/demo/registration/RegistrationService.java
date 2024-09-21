package com.example.demo.registration;

import com.example.demo.security.EmailValidator;
import com.example.demo.user.User;
import com.example.demo.user.UserRole;
import com.example.demo.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private final EmailValidator emailValidator;

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            throw new IllegalStateException("Invalid email address");
        }

        boolean usernameExists = userService.findByUsername(request.getUsername()).isPresent();
        if (usernameExists) {
            throw new IllegalStateException("Username already taken");
        }

        userService.signUpUser(
                new User(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getUsername(),
                        request.getEmail(),
                        request.getPassword(),
                        UserRole.USER
                )
        );
        return "User registered successfully";
    }
}
