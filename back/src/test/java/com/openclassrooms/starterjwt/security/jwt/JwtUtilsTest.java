package com.openclassrooms.starterjwt.security.jwt;

import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.exceptions.misusing.InvalidUseOfMatchersException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    private String username;
    private String jwtSecret;
    private int jwtExpiration;

    @Mock
    private UserDetailsImpl userDetailsImpl;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JwtUtils jwtUtils;

    @BeforeEach
    void initEach() {
        username = "jdoe@mx.com";
        jwtSecret = "aStrongSecretKey";
        jwtExpiration = 86400000;

        // Inject private values
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpiration);
    }

    @Test
    @DisplayName("Should generateJwtToken build and return a JWT String ")
    void generateJwtToken_shouldReturnJwtString() throws InvalidUseOfMatchersException {
        // GIVEN

        when(userDetailsImpl.getUsername()).thenReturn(username);
        when(authentication.getPrincipal()).thenReturn(userDetailsImpl);

        // Inject private values
        // ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        // ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpiration);

        // WHEN
        String result = jwtUtils.generateJwtToken(authentication);

        // THEN
        assertThat(result).isNotNull().isNotEmpty();
        assertThat(result.split("\\.")).hasSize(3);
        verify(userDetailsImpl).getUsername();
        verify(authentication).getPrincipal();

    }

    @Test
    @Tag("Validate")
    @DisplayName("Should getUserNameFromJwtToken")
    void getUserNameFromJwtToken_shouldReturnTheExtractedSubjectFromJWT() {
        // GIVEN

        // the following token has been generated on
        // https://www.javainuse.com/jwtgenerator with no ExpiredAt Claim and
        // sub=jdoe@mx.com
        String authToken = "eyJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoiQWRtaW4iLCJzdWIiOiJqZG9lQG14LmNvbSIsIklzc3VlciI6Iklzc3VlciIsIlVzZXJuYW1lIjoiamRvZUBteC5jb20iLCJpYXQiOjE3NDE2ODEzNTV9.w26vezDgi9H1X2hC4oYadZmIOSYarWd67fBv6QId3C3Z_172cuqbKMxHpl7U6AVSilL8RqTVxhMVma7PrX3LBQ";
        // Inject private values
        // ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        // ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpiration);
        // WHEN
        String result = jwtUtils.getUserNameFromJwtToken(authToken);

        // THEN
        assertThat(result).isEqualTo(username);
    }

    @Test
    @Tag("Validate")
    @DisplayName("Should validateJwtToken")
    void validateJwtToken_shouldReturnTrueWhenAuthTokenCanBeDecrypted() {
        // GIVEN
        // the following token has been generated on
        // https://www.javainuse.com/jwtgenerator with no ExpiredAt Claim
        String authToken = "eyJhbGciOiJIUzUxMiJ9.eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZXIiOiJZb2dhIEFwcCIsIlVzZXJuYW1lIjoiamRvZUBteC5jb20iLCJpYXQiOjE3NDE3NjU2NDN9.SWh5WQIK_qeo1QiXgJ36YuOLKr6vIMBQTkrUY0NyUltcDLnpMVC_RXpLh7CDj386Shw8PMUhEhfPjUYhByhNEw";

        // Inject private values
        // ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        // ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpiration);

        // WHEN
        boolean result = jwtUtils.validateJwtToken(authToken);
        // THEN
        assertThat(result).isTrue();
    }

    @Test
    @Tag("Validate")
    @DisplayName("Should throw SignatureException when Invalid JWT signature")
    void validateJwtToken_shouldThrowSignatureExceptionWhenInvalidJwtSignature() {
        // GIVEN
        // the following token has been generated on
        // https://www.javainuse.com/jwtgenerator with no ExpiredAt Claim and a invalid
        // encryption Algorithm
        String authToken = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZXIiOiJJc3N1ZXIiLCJVc2VybmFtZSI6IkphdmFJblVzZSIsImlhdCI6MTc0MTY4MTM1NX0.FuHECRzYSVXExjnPnrRlYbtELqJ9sHo9ZBZq0eEMd1s";

        // WHEN
        boolean result = jwtUtils.validateJwtToken(authToken);
        // THEN
        assertThat(result).isFalse();
    }

    @Test
    @Tag("Validate")
    @DisplayName("Should throw ExpiredJwtException when JWT token is expired")
    void validateJwtToken_shouldThrowExpiredJwtException() {
        // GIVEN
        // ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);

        // Set the iat one hour ago and exp 1 second ago
        String token = Jwts.builder().setSubject("username").setIssuedAt(new Date(System.currentTimeMillis() - 3600000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();

        // WHEN
        boolean result = jwtUtils.validateJwtToken(token);

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    @Tag("Validate")
    @DisplayName("Should throw SignatureException when key is invalid")
    void validateJwtToken_shouldThrowSignatureException() {
        // GIVEN

        // ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        // Sign the token with another key
        String token = Jwts.builder().setSubject("username").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, "anotherSecretKey").compact();

        // WHEN
        boolean result = jwtUtils.validateJwtToken(token);

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    @Tag("Validate")
    @DisplayName("Should throw MalformedJwtException when JWT token is malformed")
    void validateJwtToken_shouldThrowMalformedJwtException() {
        // GIVEN

        // ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);

        // a # char have been set in the header
        String token = "eyJhb#ciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        // WHEN

        boolean result = jwtUtils.validateJwtToken(token);

        // THEN
        assertThat(result).isFalse();
        // assertThrows(MalformedJwtException.class, () ->
        // Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token));

    }

    @Test
    @Tag("Validate")
    @DisplayName("Should throw UnsupportedJwtException when JWT token is unsupported")
    void validateJwtToken_shouldThrowUnsupportedJwtException() {
        // GIVEN

        // ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);

        // We ommit the signature
        String token = Jwts.builder().setSubject("username").setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)).compact();

        // WHEN
        boolean result = jwtUtils.validateJwtToken(token);

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    @Tag("Validate")
    @DisplayName("Should throw IllegalArgumentException when JWT Claim is Empty")
    void validateJwtToken_shouldThrowIllegalArgumentException() {
        // GIVEN

        // The payload has an empty claim
        // String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIiLCJpYXQiOjE2NTMwMDEyMDB9.Hz7OdxFzmw7eLHG3gfQkbVVg6K0IuAVSmm1V7jfeUN1ey4pdD-9nnp7F_vHQk-RNja2bLsToBSCgfZ5w9TAhF5I";
String token = "eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJzdWIiOiAiIn0=.KIL+3vAW6cG5uvNWBIMaN5ElsS2snIAWx6UhisAmqms=";

        // WHEN
        boolean result = jwtUtils.validateJwtToken(token);

        // THEN
        assertThat(result).isFalse();
    }
}
