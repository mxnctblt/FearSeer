package com.example.demo.appuser;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public String signUpUser(AppUser appUser) {
        // Check if email already exists
        boolean emailExists = userRepository.findByEmail(appUser.getEmail()).isPresent();
        if (emailExists) {
            throw new IllegalStateException("Email already taken");
        }

        // Check if username already exists
        boolean usernameExists = userRepository.findByUsername(appUser.getUsername()).isPresent();
        if (usernameExists) {
            throw new IllegalStateException("Username already taken");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUser.setEnabled(true);
        userRepository.save(appUser);

        return "User registered successfully";
    }

    public Optional<AppUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
