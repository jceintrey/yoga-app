package com.openclassrooms.starterjwt.controllers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private UsernamePasswordAuthenticationToken authenticationToken;
    private Authentication authentication;
    private LoginRequest loginRequest;
    private UserDetailsImpl userDetails;
    private User user;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void initEach() {

        user = new User(1L, "jdoe@mx.com", "Doe", "John", "test123", false, LocalDateTime.now(), LocalDateTime.now());

        UserDetails userDetails = new UserDetailsImpl(user.getId(), user.getEmail(), user.getFirstName(),
                user.getLastName(), user.isAdmin(), user.getPassword());

        loginRequest = new LoginRequest();
        loginRequest.setEmail(userDetails.getUsername());
        loginRequest.setPassword(userDetails.getPassword());

        // authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
        //         loginRequest.getPassword());

    }

    @Test
    @Tag("login")
    @DisplayName("Should successFully login")
    void login_shouldSuccessfullyLogin() {
        // GIVEN

        String userMail = user.getEmail();
        String jwtToken = "jwtToken";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(jwtToken);
        when(userRepository.findByEmail(userMail)).thenReturn(Optional.of(user));

        when(userDetails.getUsername()).thenReturn(userMail);
        when(userDetails.getId()).thenReturn(user.getId());
        when(userDetails.getFirstName()).thenReturn(user.getFirstName());
        when(userDetails.getLastName()).thenReturn(user.getLastName());

        // WHEN
        ResponseEntity<?> result = authController.authenticateUser(loginRequest);

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
       
    }
}
