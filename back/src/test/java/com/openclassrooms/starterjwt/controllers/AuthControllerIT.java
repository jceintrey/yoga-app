package com.openclassrooms.starterjwt.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {"/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@TestPropertySource("classpath:application-test.properties")
public class AuthControllerIT {
  @Autowired
  private MockMvc mockMvc;

  private final String userEmail = "jdoe@mx.com";
  private final String userPassword = "test123";

 // Some happy path tests
 
  @Test
  @Tag("auth")
  @DisplayName("POST /login - Should authenticate successfully and return JwtResponse")
  void login_shouldAuthenticateSuccessfully() throws Exception {

    // GIVEN
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail(userEmail);
    loginRequest.setPassword(userPassword);

    ObjectMapper om = new ObjectMapper();
    String content = om.writeValueAsString(loginRequest);

    // WHEN + THEN
    mockMvc
        .perform(MockMvcRequestBuilders.post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON).content(content))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists());
  }

  @Test
  @Tag("auth")
  @DisplayName("POST /register - Should register a user successfully")
  void register_shouldRegisterSuccessfully() throws Exception {
    // GIVEN
    SignupRequest signupRequest = new SignupRequest();
    signupRequest.setEmail("newregistered@mx.com");
    signupRequest.setFirstName("John");
    signupRequest.setLastName("Doe");
    signupRequest.setPassword(userPassword);


    ObjectMapper om = new ObjectMapper();
    String content = om.writeValueAsString(signupRequest);

    // WHEN + THEN
    mockMvc
        .perform(MockMvcRequestBuilders.post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON).content(content))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
  }
}
