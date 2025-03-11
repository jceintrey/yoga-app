package com.openclassrooms.starterjwt.security.jwt;

import java.util.Base64;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;


@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    @Mock
    private UserDetailsImpl userDetailsImpl;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JwtUtils jwtUtils;

    @BeforeEach
    void initEach() {

    }

    @Test
    @DisplayName("Should generateJwtToken build and return JWT String ")
    void generateJwtToken_shouldReturnJwtString() throws InvalidUseOfMatchersException {
        // GIVEN
        String userMail = "jdoe@mx.com";
        String jwtSecret = Base64.getEncoder().encodeToString("mySuperSecretKey".getBytes());
        int jwtExpiration = 86400000;

        when(userDetailsImpl.getUsername()).thenReturn(userMail);
        when(authentication.getPrincipal()).thenReturn(userDetailsImpl);

        // Inject private values
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpiration);

        // WHEN
        String result = jwtUtils.generateJwtToken(authentication);

        // THEN
        assertThat(result).isNotNull().isNotEmpty();
        assertThat(result.split("\\.")).hasSize(3);
        verify(userDetailsImpl).getUsername();
        verify(authentication).getPrincipal();

    }

}
