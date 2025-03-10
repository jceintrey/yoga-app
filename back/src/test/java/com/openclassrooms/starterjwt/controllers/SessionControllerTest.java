package com.openclassrooms.starterjwt.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.SessionService;

@ExtendWith(MockitoExtension.class)
public class SessionControllerTest {

    private SessionDto session1Dto, session2Dto;
    private Session session1, session2;
    private Teacher teacher;
    private User user1, user2, user3;
    private List<User> userList1, userList2;
    private List<Session> sessionList;
    private List<SessionDto> sessionListDto;

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionController sessionController;

    @BeforeEach
    void initEach() {
        teacher = new Teacher(1L, "Willis", "Bruce", LocalDateTime.now(), LocalDateTime.now());
        user1 = new User(1L, "jdoe@mx.com", "doe", "john", "test", false, null, null);
        user2 = new User(2L, "jsmith@mx.com", "smith", "john", "test", false, null, null);
        user3 = new User(2L, "bmoran@mx.com", "moran", "bob", "test", false, null, null);

        userList1 = new ArrayList<>();
        userList1.add(user1);
        userList1.add(user2);

        userList2 = new ArrayList<>();
        userList2.add(user3);

        session1 = new Session(1L, "Yoga", null, "a yoga session", teacher, userList1, null, null);
        session2 = new Session(2L, "Yoga session 2", null, "Another Yoga Session", teacher, userList2, null, null);

        sessionList = new ArrayList<>();
        sessionList.add(session1);
        sessionList.add(session2);

        session1Dto = new SessionDto(session1.getId(), "Yoga Session", new Date(), teacher.getId(),
                "This is the Yoga session 1", Arrays.asList(user1.getId(), user2.getId()), LocalDateTime.now(),
                LocalDateTime.now().plusDays(1));
        session2Dto = new SessionDto(session2.getId(), "Another Yoga Session", new Date(), teacher.getId(),
                "This is the Yoga session 2", Arrays.asList(user3.getId()), LocalDateTime.now(),
                LocalDateTime.now().plusDays(1));

        sessionListDto = new ArrayList<>();
        sessionListDto.add(session1Dto);
        sessionListDto.add(session2Dto);

    }

    @Test
    @DisplayName("Should get all sessions")
    @Tag("get")
    void findAll_shouldReturnAllSessions() throws Exception {
        // GIVEN
        when(sessionService.findAll()).thenReturn(sessionList);
        when(sessionMapper.toDto(sessionList)).thenReturn(sessionListDto);

        // WHEN
        ResponseEntity<?> result = sessionController.findAll();

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(sessionListDto);

    }

    @ParameterizedTest(name = "Session exists: {0}")
    @CsvSource({ "true", "false" })
    @Tag("get")
    @DisplayName("Should get session by its Id or not Found if not exists")
    void find_shouldReturnSessionWhenExistAndNotFoundIfNotExists(boolean exists) throws Exception {
        // GIVEN
        when(sessionService.getById(1L)).thenReturn(exists ? session1 : null);
        if (exists)
            when(sessionMapper.toDto(session1)).thenReturn(session1Dto);

        // WHEN
        ResponseEntity<?> result = sessionController.findById("1");

        // THEN

        assertThat(result.getStatusCode()).isEqualTo(exists ? HttpStatus.OK : HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isEqualTo(exists ? session1Dto : null);

    }

    @Test
    @Tag("get")
    @DisplayName("Should return bad request when the session Id is not valid")
    void find_shouldReturnBadRequestWhenIdIsNotValid() {
        // GIVEN
        String invalidSessionId = "invalidSessionId";

        // WHEN
        ResponseEntity<?> result = sessionController.findById(invalidSessionId);

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should create a session given its body parameters")
    @Tag("create")
    void create_shouldCreateSessionGivenItsBodyParameters() {
        // GIVEN
        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session1);
        when(sessionMapper.toDto(any(Session.class))).thenReturn(session1Dto);
        when(sessionService.create(session1)).thenReturn(session1);

        // WHEN
        ResponseEntity<?> result = sessionController.create(session1Dto);

        // THEN
        verify(sessionMapper).toEntity(session1Dto);
        verify(sessionMapper).toDto(session1);
        verify(sessionService).create(session1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(session1Dto);

    }

    @Test
    @Tag("update")
    @DisplayName("Should update a session given its ID and body parameters")
    void update_shouldUpdateSessionGivenItsIDAndBodyParameters() {
        // GIVEN
        session1Dto.setDescription("Let's change the description to another one");

        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session1);
        when(sessionService.update(any(Long.class), any(Session.class))).thenReturn(session1);
        when(sessionMapper.toDto(any(Session.class))).thenReturn(session1Dto);

        // WHEN
        ResponseEntity<?> result = sessionController.update("" + session1.getId(), session1Dto);
        // THEN
        verify(sessionMapper).toEntity(session1Dto);
        verify(sessionMapper).toDto(session1);
        verify(sessionService).update(session1.getId(), session1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).extracting("description").asString().contains("Let's change th");

    }

    @Test
    @Tag("update")
    @DisplayName("Should return bad request when the session Id is not valid")
    void update_shouldReturnBadRequestWhenIdIsNotValid() {
        // GIVEN
        String invalidSessionId = "invalidSessionId";

        // WHEN
        ResponseEntity<?> result = sessionController.update(invalidSessionId, session1Dto);

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest(name = "Session exists {0}")
    @CsvSource({ "true", "false" })
    @Tag("delete")
    @DisplayName("Should delete the session given its Id if session exists")
    void delete_shouldDeleteSessiongivenItsIdwhenExist(boolean exist) {
        // GIVEN
        Long sessionID = session1.getId();
        when(sessionService.getById(sessionID)).thenReturn(exist ? session1 : null);
        if (exist)
            doNothing().when(sessionService).delete(sessionID);
        // WHEN

        ResponseEntity<?> result = sessionController.save("" + sessionID); // /!\ Should rename the
                                                                           // sessionsController save methode for
        // @DeleteMapping to a more explicit name

        // THEN
        if (exist)
            verify(sessionService).delete(sessionID);
        assertThat(result.getStatusCode()).isEqualTo(exist ? HttpStatus.OK : HttpStatus.NOT_FOUND);

    }

    @Test
    @Tag("delete")
    @DisplayName("Should return bad request when the session Id is not valid")
    void delete_shouldReturnBadRequestWhenIdIsNotValid() {
        // GIVEN
        String invalidSessionId = "invalidSessionId";

        // WHEN
        ResponseEntity<?> result = sessionController.save(invalidSessionId);

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    
    @Test
    @Tag("participate")
    @DisplayName("Should participate to Session given the session Id and the userId")
    void participate_shouldParticipateGivenUserIdAndSessionId() {
        // GIVEN
        Long sessionId = session1.getId();
        Long userId = user1.getId();
        doNothing().when(sessionService).participate(sessionId, userId);

        // WHEN
        ResponseEntity<?> result = sessionController.participate(sessionId + "", userId + "");
        // THEN
        verify(sessionService).participate(sessionId, userId);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Tag("participate")
    @DisplayName("Should return bad request when the session Id or user Id are not valid")
    void participate_shouldReturnBadRequestWhenIdsAreNotValid() {
        // GIVEN
        String invalidSessionId = "invalidSessionId";
        String invalidUserId = "invalidUserId";

        // WHEN
        ResponseEntity<?> result = sessionController.participate(invalidSessionId, invalidUserId);

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Tag("participate")
    @DisplayName("Should no longer participate to session given the session Id and the userId")
    void participate_shouldnoLongerParticipateGivenUserIdAndSessionId() {
        // GIVEN
        Long sessionId = session1.getId();
        Long userId = user1.getId();
        doNothing().when(sessionService).noLongerParticipate(sessionId, userId);

        // WHEN
        ResponseEntity<?> result = sessionController.noLongerParticipate(sessionId + "", userId + "");
        // THEN
        verify(sessionService).noLongerParticipate(sessionId, userId);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Tag("participate")
    @DisplayName("Should return bad request when the session Id or user Id are not valid")
    void nolongerparticipate_shouldReturnBadRequestWhenIdsAreNotValid() {
        // GIVEN
        String invalidSessionId = "invalidSessionId";
        String invalidUserId = "invalidUserId";

        // WHEN
        ResponseEntity<?> result = sessionController.noLongerParticipate(invalidSessionId, invalidUserId);

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

}
