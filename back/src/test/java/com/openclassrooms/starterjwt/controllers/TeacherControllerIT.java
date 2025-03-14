package com.openclassrooms.starterjwt.controllers;

import java.util.Date;
import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
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
@Sql(scripts = { "/init.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@TestPropertySource("classpath:application-test.properties")
@EntityScan(basePackages = "com.openclassrooms.starterjwt.models")
public class TeacherControllerIT {
 @Autowired
    private MockMvc mockMvc;
@Value("${oc.app.jwtSecret}")
    private String secretKey;

   private String adminToken;
    private String adminEmailTest;
    private String teacherId;


 @BeforeEach
    void initEach() {

        
        adminEmailTest="jdoe@mx.com";
        teacherId = "1";
        assertThat(teacherId).isNotEmpty();

        // test token generation
        adminToken = Jwts.builder().setClaims(new HashMap<>()).setSubject(adminEmailTest)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, secretKey).compact();
    }
 @Test
    @DisplayName("@GetMapping(\"/{id}\") should return the teacher with the expected teacherId")
    void get_shouldReturnTeacherWhenExistingId() throws Exception {
        // GIVEN
        
        // WHEN + THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/teacher/" + teacherId).header("Authorization", "Bearer " + adminToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Willis"));
    }

    @Test
    @DisplayName("@GetMapping(\"/{id}\") should return Httpstatus NotFound when the teacherId does not exist ")
    void get_shouldReturnNotFoundWhenGetNonExistingTeacher() throws Exception {
        // GIVEN
        String id = "10";
        // WHEN + THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/teacher/" + id).header("Authorization", "Bearer " + adminToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }
    @Test
    @DisplayName(" @GetMapping() should return All teachers")
    void get_shouldReturnAllTeachers() throws Exception {
        // GIVEN
        
        // WHEN + THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/teacher").header("Authorization", "Bearer " + adminToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
    }

}
