package com.openclassrooms.starterjwt.security.jwt;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@ExtendWith(MockitoExtension.class)
public class AuthTokenFilterTest {
    private String token, username;
    private UserDetailsImpl userDetails;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @BeforeEach
    void initEach() {
        username = "jdoe@mx.com";
        userDetails = new UserDetailsImpl(1L, username, "John", "Doe", false, "test123");

        token = Jwts.builder().setSubject(username).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, "aStrongSecretKey").compact();

    }

    @Test
    void doFilterInternal_shouldThrowExceptionAndDoFilterWhenUserNotFound() throws IOException, ServletException {
        // GIVEN
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtUtils.validateJwtToken(anyString())).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(anyString())).thenReturn(username);
        when(userDetailsServiceImpl.loadUserByUsername(anyString())).thenReturn(null);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // WHEN
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // THEN
        verify(jwtUtils).validateJwtToken(token);
        verify(jwtUtils).getUserNameFromJwtToken(token);
        verify(userDetailsServiceImpl).loadUserByUsername(username);
        verify(filterChain).doFilter(request, response);

    }
    @Test
    void doFilterInternal_shouldOnlyPassHttpRequest() throws IOException, ServletException {
        // GIVEN
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "NoBearerSet " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // WHEN
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // THEN
        verify(filterChain).doFilter(request, response);

    }

    @Test
    void doFilterInternal_shouldSetAuthenticationAndDoFilter() throws IOException, ServletException {
        // GIVEN
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtUtils.validateJwtToken(anyString())).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(anyString())).thenReturn(username);
        when(userDetailsServiceImpl.loadUserByUsername(anyString())).thenReturn(userDetails);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // WHEN
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // THEN
        verify(jwtUtils).validateJwtToken(token);
        verify(jwtUtils).getUserNameFromJwtToken(token);
        verify(userDetailsServiceImpl).loadUserByUsername(username);
        verify(filterChain).doFilter(request, response);

    }
}
