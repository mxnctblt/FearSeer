package com.example.demo.user;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class User implements UserDetails {

    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private Long id;

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] profilePicture;

    private String bio;

    public User(String firstName,
                String lastName,
                String username,
                String email,
                String password,
                UserRole userRole) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.profilePicture = defaultProfilePicture();
        this.bio = "";
    }

    private byte[] defaultProfilePicture() {
        try {
            // Load default profile picture from resources
            ClassPathResource resource = new ClassPathResource("static/media/default-profile.jpg");
            // Get the file size to check if it's too large
            long fileSize = resource.getFile().length(); // Get the size of the file
            if (fileSize > 16 * 1024 * 1024) { // Assuming we want to cap at 16MB
                throw new IllegalStateException("Default profile picture is too large.");
            }
            return Files.readAllBytes(resource.getFile().toPath());
        } catch (IOException e) {
            throw new IllegalStateException("Default profile picture could not be loaded", e);
        }
    }

    // Other methods for Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getUserUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
