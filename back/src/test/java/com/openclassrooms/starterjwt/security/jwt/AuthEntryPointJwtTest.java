package com.openclassrooms.starterjwt.security.jwt;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class AuthEntryPointJwtTest {


    @InjectMocks
    private AuthEntryPointJwt authEntryPointJwt;

    @Test
    void testCommence_ShouldUpdateResponse() throws IOException, ServletException {
        // GIVEN

        String path = "/error-path";
        String errorMessage = "Unauthorized error message";

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        AuthenticationException authException = new AuthenticationException(errorMessage) {};
        request.setServletPath(path);

        // WHEN
        authEntryPointJwt.commence(request, response, authException);

        // THEN
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> parsedData =
                objectMapper.readValue(response.getContentAsString(), Map.class);


        assertThat(parsedData.get("status")).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(parsedData.get("error")).isEqualTo("Unauthorized");
        assertThat(parsedData.get("message")).isEqualTo(errorMessage);
        assertThat(parsedData.get("path")).isEqualTo(path);

    }
}
