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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class AuthControllerTest {

    private LoginRequest loginRequest;

    private UserDetailsImpl userDetails;
    private User user;
    private SecurityContext securityContext;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

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

        userDetails = new UserDetailsImpl(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.isAdmin(), user.getPassword());

        loginRequest = new LoginRequest();
        loginRequest.setEmail(userDetails.getUsername());
        loginRequest.setPassword(userDetails.getPassword());
        log.info("InitEach a chaque test");

    }

    @Test
    @Tag("login")
    @DisplayName("Should successFully login")
    void login_shouldSuccessfullyLogin() {
        // GIVEN

        String userMail = user.getEmail();
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(jwtToken);
        when(userRepository.findByEmail(userMail)).thenReturn(Optional.of(user));

        // WHEN
        ResponseEntity<?> result = authController.authenticateUser(loginRequest);

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        JwtResponse jwtResponse = (JwtResponse) result.getBody();
        log.info(jwtResponse.getToken());

        assertThat(jwtResponse.getToken()).isEqualTo(jwtToken);
        assertThat(jwtResponse.getId()).isEqualTo(user.getId());
        assertThat(jwtResponse.getUsername()).isEqualTo(user.getEmail());
        assertThat(jwtResponse.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(jwtResponse.getLastName()).isEqualTo(user.getLastName());
        assertThat(jwtResponse.getAdmin()).isEqualTo(user.isAdmin());

    }

    @Test
    @Tag("register")
    @DisplayName("Should successFully register")
    void register_shouldSuccessfullyRegister() {
        // GIVEN

        String rawPassword = "acleartextpassword";

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail(user.getEmail());
        signupRequest.setFirstName(user.getFirstName());
        signupRequest.setLastName(user.getLastName());
        signupRequest.setPassword(rawPassword);

        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(user.getPassword());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // WHEN
        ResponseEntity<?> result = authController.registerUser(signupRequest);

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userRepository).save(any(User.class));
        assertThat(result.getBody()).extracting("message").asString().contains("User registered successfully!");

    }

    @Test
    @Tag("register")
    @DisplayName("Should return bad request when user is already taken")
    void find_shouldReturnBadRequestWhenUserAlreadyTaken() {
        // GIVEN
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail(user.getEmail());
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

        // WHEN
        ResponseEntity<?> result = authController.registerUser(signupRequest);

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).extracting("message").asString().contains("Error: Email is already taken!");

    }
}
