package com.openclassrooms.starterjwt.controllers;

import java.util.Date;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
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
@EntityScan(basePackages = "com.openclassrooms.starterjwt.models")
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


        // test token generation
        adminToken = Jwts.builder().setClaims(new HashMap<>()).setSubject(adminEmailTest)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, secretKey).compact();

        userToken = Jwts.builder().setClaims(new HashMap<>()).setSubject(userEmailTest)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, secretKey).compact();


    }

    @Test
    @Tag("get")
    @DisplayName("@GetMapping(\"/{id}\") should return the session with the expected sessionId")
    void get_shouldReturnSessionWhenExistingId() throws Exception {
        // GIVEN
        String sessionId = "1";
        // WHEN + THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/session/" + sessionId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Yoga Session"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"));
    }

    @Test
    @Tag("get")
    @DisplayName("@GetMapping(\"/{id}\") should return Httpstatus NotFound when the sessionId does not exist ")
    void get_shouldReturnNotFoundWhenNonExistingSession() throws Exception {
        // GIVEN
        String sessionId = "10";
        // WHEN + THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/session/" + sessionId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @Tag("get")
    @DisplayName("@GetMapping() should return All sessions")
    void get_shouldReturnAllSessions() throws Exception {
        // GIVEN

        // WHEN + THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/session").header("Authorization",
                "Bearer " + adminToken)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
    }

    @Test
    @Tag("create")
    @DisplayName("@PostMapping() should create a session")
    void create_shouldCreateSession() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("New Yoga Session"));
    }

    @Test
    @Tag("update")
    @DisplayName("@PostMapping() should update a session")
    void get_shouldUpdateSession() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("New Yoga Session"));
    }

    @Test
    @Tag("delete")
    @DisplayName("@DeleteMapping(\"{id}\") should delete a session")
    void delete_shouldDeleteSession() throws Exception {
        // GIVEN
        String sessionId = "1";

        // WHEN + THEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/" + sessionId)
                .header("Authorization", "Bearer " + userToken))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    @Tag("participate")
    @DisplayName("@PostMapping() should participate to a session")
    void participate_shouldParticipateSession() throws Exception {
        // GIVEN
        String sessionId = "1";
        String userId = "1";


        // WHEN + THEN
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/session/" + sessionId + "/participate/" + userId)
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Tag("participate")
    @DisplayName("@PostMapping() should not participate if already participating")
    void participate_shouldParticipateSession2() throws Exception {
        // GIVEN
        String sessionId = "2";
        String userId = "2";


        // WHEN + THEN
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/session/" + sessionId + "/participate/" + userId)
                        .header("Authorization", "Bearer " + adminToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


}
