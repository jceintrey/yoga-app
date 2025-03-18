package com.openclassrooms.starterjwt.controllers;

import java.util.Date;
import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {"/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@TestPropertySource("classpath:application-test.properties")
public class UserControllerIT {
    @Autowired
    private MockMvc mockMvc;


    @Value("${oc.app.jwtSecret}")
    private String secretKey;

    private String token;
    private String userEmail;
    private String userId;

    @BeforeEach
    void initEach() {


        userEmail = "jdoe@mx.com";
        userId = "1";
        assertThat(userId).isNotEmpty();

        token = Jwts.builder().setClaims(new HashMap<>()).setSubject(userEmail)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, secretKey).compact();
    }


    // Some happy path tests

    @Test
    @Tag("user")
    @DisplayName("GET /user/{id} - Should return user details for existing userId")
    void get_shouldReturnUserWhenExistingId() throws Exception {
        // GIVEN

        // WHEN + THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/" + userId).header("Authorization",
                "Bearer " + token)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userEmail));
    }

    @Test
    @Tag("user")
    @DisplayName("DELETE /user/{id} - Should delete existing user")
    void delete_shouldDeleteExistingUser() throws Exception {
        // GIVEN


        // WHEN + THEN

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/" + userId).header("Authorization",
                "Bearer " + token)).andExpect(MockMvcResultMatchers.status().isOk());

    }

    // Some edge case tests

    @Test
    @Tag("user")
    @DisplayName("DELETE /user/{id} - Should return NotFound when userId does not exist")
    void delete_shouldReturnNotFoundWhenDeleteNonExistingUser() throws Exception {
        // GIVEN
        String id = "10";
        // WHEN + THEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/" + id).header("Authorization",
                "Bearer " + token)).andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @Tag("user")
    @DisplayName("DELETE /user/{id} - Should return Unauthorized when deleting another user")
    void delete_shouldReturnUnauthorizedWhenDeleteAnotherUser() throws Exception {
        // GIVEN
        String id = "2";


        // WHEN + THEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/" + id).header("Authorization",
                "Bearer " + token)).andExpect(MockMvcResultMatchers.status().isUnauthorized());

    }
}
