package com.openclassrooms.starterjwt.services;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

@ExtendWith(MockitoExtension.class)

public class UserServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(TeacherServiceTest.class);

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        logger.info("Unit test: {}", testInfo.getDisplayName());
    }

    @Test
    void deleteUser_shouldDeleteUserFromRepository() {
        // GIVEN
        Long id = 1L;

        // WHEN
        userService.delete(id);

        // THEN
        verify(userRepository).deleteById(id);
    }

    @Test
    void findById_shouldReturnTheUserIfExist() {
        // GIVEN
        Long id = 1L;
        User expectedUser = new User(id, "jdoe@mx.com", "doe", "john", "test", false, null, null);

        when(userRepository.findById(id)).thenReturn(Optional.of(expectedUser));

        // WHEN
        User actualUser = userService.findById(id);

        // THEN
        verify(userRepository).findById(id);
        assertThat(actualUser).isNotNull();
        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    void finById_shouldReturnNullIfNotExist() {
        // GIVEN
        Long id = 999L;
        User expectedUser = null;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN
        User actualUser = userService.findById(id);

        // THEN
        verify(userRepository).findById(id);
        assertThat(actualUser).isNull();

    }
}
