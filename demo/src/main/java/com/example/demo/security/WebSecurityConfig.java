package com.example.demo.security.config;

import com.example.demo.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/register", "/auth/login").permitAll() // Allow registration and login pages
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/auth/login") // Custom login page
                        .defaultSuccessUrl("/", true) // Redirect to home page after successful login
                        .permitAll() // Allow access to login for everyone
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout") // Custom logout URL
                        .logoutSuccessUrl("/auth/login?logout") // Redirect to login page after logout
                        .permitAll()
                )
                .authenticationProvider(daoAuthenticationProvider()); // Add custom authentication provider

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setPasswordEncoder(bCryptPasswordEncoder);
        auth.setUserDetailsService(userService); // Use custom user service for authentication
        return auth;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
