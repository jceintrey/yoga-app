package com.openclassrooms.starterjwt.controllers;

import java.util.Date;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {"/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@TestPropertySource("classpath:application-test.properties")
public class SessionControllerIT {
        @Autowired
        private MockMvc mockMvc;
        @Value("${oc.app.jwtSecret}")
        private String secretKey;

        private String adminToken, userToken;
        private String adminEmailTest, userEmailTest;


        @BeforeEach
        void initEach() {

                adminEmailTest = "jdoe@mx.com";
                userEmailTest = "jsmith@mx.com";

                adminToken = Jwts.builder().setClaims(new HashMap<>()).setSubject(adminEmailTest)
                                .setIssuedAt(new Date(System.currentTimeMillis()))
                                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                                .signWith(SignatureAlgorithm.HS512, secretKey).compact();

                userToken = Jwts.builder().setClaims(new HashMap<>()).setSubject(userEmailTest)
                                .setIssuedAt(new Date(System.currentTimeMillis()))
                                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                                .signWith(SignatureAlgorithm.HS512, secretKey).compact();

        }

        // Some happy path tests

        @Test
        @Tag("session")
        @DisplayName("GET /{id} - Should return the session with the expected ID")
        void getSessionById_shouldReturnExpectedSession() throws Exception {
                // GIVEN
                String sessionId = "1";
                // WHEN + THEN
                mockMvc.perform(MockMvcRequestBuilders.get("/api/session/" + sessionId)
                                .header("Authorization", "Bearer " + adminToken))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                                                .value("Yoga Session"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"));
        }

        @Test
        @Tag("session")
        @DisplayName("GET / - Should return all sessions")
        void getAllSessions_shouldReturnAllSessions() throws Exception {
                // GIVEN

                // WHEN + THEN
                mockMvc.perform(MockMvcRequestBuilders.get("/api/session").header("Authorization",
                                "Bearer " + adminToken))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
        }

        @Test
        @Tag("session")
        @DisplayName("POST / - Should create a new session")
        void createSession_shouldSucceed() throws Exception {
                // GIVEN

                ObjectMapper objectMapper = new ObjectMapper();
                SessionDto sessionDto = new SessionDto();
                sessionDto.setName("New Yoga Session");
                sessionDto.setDescription("This is an new Yoga session");
                sessionDto.setDate(new Date());
                sessionDto.setTeacher_id(1L);
                String jsonString = objectMapper.writeValueAsString(sessionDto);

                // WHEN + THEN
                mockMvc.perform(MockMvcRequestBuilders.post("/api/session")
                                .header("Authorization", "Bearer " + adminToken).content(jsonString)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                                .andDo(MockMvcResultHandlers.print())
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                                                .value("New Yoga Session"));
        }



        @Test
        @Tag("session")
        @DisplayName("PUT /{id} - Should update a session")
        void updateSession_shouldSucceed() throws Exception {
                // GIVEN
                String sessionId = "1";
                ObjectMapper objectMapper = new ObjectMapper();
                SessionDto sessionDto = new SessionDto();
                sessionDto.setName("New Yoga Session");
                sessionDto.setDescription("This is an new Yoga session");
                sessionDto.setDate(new Date());
                sessionDto.setTeacher_id(1L);
                String jsonString = objectMapper.writeValueAsString(sessionDto);

                // WHEN + THEN
                mockMvc.perform(MockMvcRequestBuilders.put("/api/session/" + sessionId)
                                .header("Authorization", "Bearer " + adminToken).content(jsonString)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                                                .value("New Yoga Session"));
        }

        @Test
        @Tag("session")
        @DisplayName("DELETE /{id} - Should delete a session")
        void deleteSession_shouldSucceed() throws Exception {
                // GIVEN
                String sessionId = "1";

                // WHEN + THEN
                mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/" + sessionId)
                                .header("Authorization", "Bearer " + userToken))
                                .andExpect(MockMvcResultMatchers.status().isOk());

        }

        @Test
        @Tag("participation")
        @DisplayName("POST /{id}/participate/{userId} - Should participate in a session")
        void participateInSession_shouldSucceed() throws Exception {
                // GIVEN
                String sessionId = "1";
                String userId = "1";


                // WHEN + THEN
                mockMvc.perform(MockMvcRequestBuilders
                                .post("/api/session/" + sessionId + "/participate/" + userId)
                                .header("Authorization", "Bearer " + adminToken))
                                .andDo(MockMvcResultHandlers.print())
                                .andExpect(MockMvcResultMatchers.status().isOk());
        }

        @Test
        @Tag("participation")
        @DisplayName("DELETE /{id}/participate/{userId} - Should unparticipate from a session")
        void unparticipateFromSession_shouldSucceed() throws Exception {
                // GIVEN
                String sessionId = "2";
                String userId = "2";


                // WHEN + THEN
                mockMvc.perform(MockMvcRequestBuilders
                                .delete("/api/session/" + sessionId + "/participate/" + userId)
                                .header("Authorization", "Bearer " + userToken))
                                .andDo(MockMvcResultHandlers.print())
                                .andExpect(MockMvcResultMatchers.status().isOk());
        }



        // Some edge case tests

        @Test
        @Tag("participation")
        @DisplayName("POST /{id}/participate/{userId} - Should fail if already participating")
        void participateInSession_shouldFailIfAlreadyParticipating() throws Exception {
                // GIVEN
                String sessionId = "2";
                String userId = "2";


                // WHEN + THEN
                mockMvc.perform(MockMvcRequestBuilders
                                .post("/api/session/" + sessionId + "/participate/" + userId)
                                .header("Authorization", "Bearer " + adminToken))
                                .andDo(MockMvcResultHandlers.print())
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        @Tag("session")
        @DisplayName("GET /{id} - Should return 404 if session does not exist")
        void getSessionById_shouldReturnNotFoundIfNonExisting() throws Exception {
                // GIVEN
                String sessionId = "10";
                // WHEN + THEN
                mockMvc.perform(MockMvcRequestBuilders.get("/api/session/" + sessionId)
                                .header("Authorization", "Bearer " + adminToken))
                                .andExpect(MockMvcResultMatchers.status().isNotFound());

        }


}
