package com.openclassrooms.starterjwt.services;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(TeacherServiceTest.class);

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        logger.info("Unit test: {}", testInfo.getDisplayName());
    }

    private static Session getRandomSession() {
        Random random = new Random();
        Long sessionId = Long.valueOf(1 + random.nextInt(1000));
        
        return new Session(sessionId, "Yoga", null, "a yoga session", new Teacher(), new ArrayList<User>(), null, null);
    }

    @Test
    @DisplayName("Should create a new session")
    void create_shouldSaveTheGivenSessionToRepository() {
        // GIVEN
        Session expectedSession = SessionServiceTest.getRandomSession();

        when(sessionRepository.save(expectedSession)).thenReturn(expectedSession);
        // WHEN
        Session actualSession = sessionService.create(expectedSession);

        // THEN
        verify(sessionRepository).save(expectedSession);
        assertThat(expectedSession).isEqualTo(actualSession);
        assertThat(actualSession).isNotNull();

    }

    @Test
    @DisplayName("Should delete the session with Id")
    void create_shouldSessionGivenItsId() {
        // GIVEN
        Long id = 1L;

        // WHEN
        sessionService.delete(id);

        // THEN
        verify(sessionRepository).deleteById(id);

    }

    @Test
    @DisplayName("Shoudl find and return All sessions")
    void findAll_shouldReturnAllSessions() {
        // GIVEN
        List<Session> sessions = List.of(SessionServiceTest.getRandomSession(), SessionServiceTest.getRandomSession());
        Long expectedId1 = sessions.get(0).getId();
        Long expectedId2 = sessions.get(1).getId();

        when(sessionRepository.findAll()).thenReturn(sessions);

        // WHEN
        List<Session> result = sessionService.findAll();

        // THEN
        verify(sessionRepository).findAll();
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Session::getId).containsExactly(expectedId1, expectedId2);
    }

    @ParameterizedTest
    @CsvSource({ "true", "false" })
    void findById_shouldReturnSessionOrNullIfNotFound(boolean exists) {

        // GIVEN
        Session expectedSession = exists ? SessionServiceTest.getRandomSession() : null;
        Long id = exists ? expectedSession.getId() : null;

        when(sessionRepository.findById(id)).thenReturn(Optional.ofNullable(expectedSession));

        // WHEN
        Session actualSession = sessionService.getById(id);

        // THEN
        if (exists) {
            verify(sessionRepository).findById(id);
            assertThat(actualSession).isEqualTo(expectedSession);
        } else {
            assertThat(actualSession).isNull();
        }
    }

    @Test
    @DisplayName("Should update a session")
    void update_shouldSaveTheGivenSessionWithTheIdToRepository() {
        // GIVEN
        Long expectedId = 1L;
        Session expectedSession = SessionServiceTest.getRandomSession();

        Session sessionToUpdate = new Session();
        BeanUtils.copyProperties(expectedSession, sessionToUpdate);

        logger.info(MessageFormat.format("The session {2} with id {1} will be updated with id {0} ", expectedId,
                sessionToUpdate.getId(), sessionToUpdate.getName()));

        when(sessionRepository.save(sessionToUpdate)).thenReturn(sessionToUpdate);

        // WHEN
        Session actualSession = sessionService.update(expectedId, sessionToUpdate);
        logger.info(MessageFormat.format("The actual session {1} has the id {0} ", actualSession.getId(),
                actualSession.getName()));
        // THEN
        assertThat(actualSession).isNotNull();
        assertThat(actualSession.getId()).isEqualTo(expectedId);
        verify(sessionRepository).save(sessionToUpdate);
    }

    @Test
    @Tag("Participate")
    @DisplayName("Should add the user to session given the user and the session exist and the user is not already participating")
    void participate_shouldAddAnExistingAndNonParticipatingUserToAnExistingSession() {
        // GIVEN
        Session expectedSession = SessionServiceTest.getRandomSession();
        expectedSession.setUsers(new ArrayList<>());
        Long expectedSessionId = expectedSession.getId();
        Long expectedUserId = 2L;
        User expectedUser = new User(expectedUserId, "jsmith@mx.com", "smith", "john", "test", false, null, null);

        when(sessionRepository.findById(expectedSessionId)).thenReturn(Optional.of(expectedSession));
        when(userRepository.findById(expectedUserId)).thenReturn(Optional.of(expectedUser));
        when(sessionRepository.save(expectedSession)).thenReturn(expectedSession);

        // WHEN
        sessionService.participate(expectedSessionId, expectedUserId);

        // THEN

        assertThat(expectedSession.getUsers()).contains(expectedUser);
        verify(sessionRepository).save(expectedSession);

    }

    @ParameterizedTest(name = "sessionIdExists {0} , userIdExists {1}")
    @CsvSource({ "false, true", "true, false", "false, false" })
    @Tag("Participate")
    @DisplayName("Should throw NotFoundException on participate if session or user not found")
    void participate_shouldThrowNotFoundExceptionOnParticipateWhebUserOrSessionNotFound(boolean sessionIdExists,
            boolean userIdExists) {
        // GIVEN
        User expectedUser = new User(88L, "jdoe@mx.com", "doe", "john", "test", false, null, null);
        Long expectedUserId = expectedUser.getId();
        Session expectedSession = SessionServiceTest.getRandomSession();
        Long expectedSessionId = expectedSession.getId();

        if (!sessionIdExists) {
            when(sessionRepository.findById(expectedSessionId)).thenReturn(Optional.empty());
        } else {
            when(sessionRepository.findById(expectedSessionId)).thenReturn(Optional.ofNullable(expectedSession));
        }
        if (!userIdExists) {
            when(userRepository.findById(expectedUserId)).thenReturn(Optional.empty());
        } else {
            when(userRepository.findById(expectedUserId)).thenReturn(Optional.ofNullable(expectedUser));
        }

        // WHEN + THEN
        assertThrows(NotFoundException.class, () -> sessionService.participate(expectedSessionId, expectedUserId));

        verify(sessionRepository, times(1)).findById(expectedSessionId);
        verify(userRepository, times(1)).findById(expectedUserId);
        verify(sessionRepository, never()).save(any());
    }

    @Test
    @Tag("Participate")
    @DisplayName("Should throw BadRequestException on participate when user is already participating")
    void participate_shouldThrowBadRequestExceptionWhenUserIsAlreadyParticipating() {
        // GIVEN
        User expectedUser = new User(88L, "jdoe@mx.com", "doe", "john", "test", false, null, null);
        Long expectedUserId = expectedUser.getId();
        Session expectedSession = SessionServiceTest.getRandomSession();
        Long expectedSessionId = expectedSession.getId();

        if (expectedSession.getUsers() == null) {
            expectedSession.setUsers(new ArrayList<>());
        } else {
            expectedSession.setUsers(new ArrayList<>(expectedSession.getUsers()));
        }

        expectedSession.getUsers().add(expectedUser);

        when(sessionRepository.findById(expectedSessionId)).thenReturn(Optional.of(expectedSession));
        when(userRepository.findById(expectedUserId)).thenReturn(Optional.of(expectedUser));

        // WHEN + THEN
        assertThrows(BadRequestException.class, () -> sessionService.participate(expectedSessionId, expectedUserId));
        verify(sessionRepository, never()).save(any());
    }

    @Test
    @Tag("noLongerParticipate")
    @DisplayName("Should throw BadRequestException on noLongerParticipate when user is already not participating")
    void noLongerParticipate_shouldThrowBadRequestExceptionWhenUserIsAlreadyNotParticipating() {
        // GIVEN
        Session expectedSession = SessionServiceTest.getRandomSession();
        Long expectedSessionId = expectedSession.getId();
        expectedSession.setUsers(new ArrayList<>());
        Long expectedUserId = 1L;

        when(sessionRepository.findById(expectedSessionId)).thenReturn(Optional.ofNullable(expectedSession));

        // WHEN + THEN
        assertThrows(BadRequestException.class,
                () -> sessionService.noLongerParticipate(expectedSessionId, expectedUserId));
        verify(sessionRepository, never()).save(any());

    }

    @Test
    @Tag("noLongerParticipate")
    @DisplayName("Should throw NotFoundException on noLongerParticipate when session not exists")
    void noLongerParticipate_shouldThrowNotFoundExceptionWhenSessionNotExists() {
        // GIVEN
        Long expectedSessionId = 1L;
        Long expectedUserId = 1L;

        when(sessionRepository.findById(expectedSessionId)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(NotFoundException.class,
                () -> sessionService.noLongerParticipate(expectedSessionId, expectedUserId));
        verify(sessionRepository, never()).save(any());

    }

    @Test
    @Tag("noLongerParticipate")
    @DisplayName("Should remove User from session users when session Exist and user is participating")
    void noLongerParticipate_shouldRemoveUserFromSessionUsersWhenSessionExistsAndUserIsParticipating() {
        // GIVEN
        Session expectedSession = SessionServiceTest.getRandomSession();
        expectedSession.setUsers(new ArrayList<>());
        Long expectedUserId = 88L;
        User expectedUser = new User(expectedUserId, "jdoe@mx.com", "doe", "john", "test", false, null, null);
        expectedSession.getUsers().add(expectedUser);

        Session sessionToUpdate = new Session();
        BeanUtils.copyProperties(expectedSession, sessionToUpdate);
        Long sessionToUpdateId = expectedSession.getId();

        when(sessionRepository.findById(sessionToUpdateId)).thenReturn(Optional.ofNullable(sessionToUpdate));

        // WHEN
        sessionService.noLongerParticipate(sessionToUpdateId, expectedUserId);

        // THEN
        verify(sessionRepository, times(1)).findById(sessionToUpdateId);
        assertThat(sessionToUpdate.getUsers()).doesNotContain(expectedUser);

    }

    @Test
    @Tag("noLongerParticipate")
    @DisplayName("Should throw BadRequestException when session has null users list")
    void noLongerParticipate_shouldHandleNullUserList() {
        // GIVEN
        Session expectedSession = SessionServiceTest.getRandomSession();
        expectedSession.setUsers(null);

        Long expectedUserId = 88L;

        when(sessionRepository.findById(expectedSession.getId())).thenReturn(Optional.of(expectedSession));

        // WHEN + THEN
        assertThrows(NullPointerException.class,
                () -> sessionService.noLongerParticipate(expectedSession.getId(), expectedUserId));

        verify(sessionRepository, never()).save(any());
    }

}
