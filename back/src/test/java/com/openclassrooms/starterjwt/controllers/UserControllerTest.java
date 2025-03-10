package com.openclassrooms.starterjwt.controllers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    private User user1, user2;
    private UserDto user1Dto, user2Dto;
    private List<User> userList;
    private List<UserDto> userDtoList;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UserDetails userDetails;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void initEach() {
        user1 = new User(1L, "jdoe@mx.com", "doe", "john", "test", false, null, null);
        user2 = new User(2L, "jsmith@mx.com", "smith", "john", "test", false, null, null);

        userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        userDtoList = new ArrayList<>();
        userDtoList.add(user1Dto);
        userDtoList.add(user2Dto);

        SecurityContextHolder.setContext(securityContext);

    }

    @ParameterizedTest(name = "User exist {0}")
    @CsvSource({ "true", "false" })
    @Tag("get")
    @DisplayName("Should return user when exist and not found either")
    void find_shouldReturnUserWhenExist(boolean exist) {
        // GIVEN
        Long userId = user1.getId();
        when(userService.findById(userId)).thenReturn(exist ? user1 : null);

        if (exist)
            when(userMapper.toDto(user1)).thenReturn(user1Dto);
        // WHEN
        ResponseEntity<?> result = userController.findById("" + userId);

        // THEN
        if (exist) {
            verify(userService).findById(userId);
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isEqualTo(user1Dto);

        } else {
            verify(userService).findById(userId);
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Test
    @Tag("get")
    @DisplayName("Should return bad request when id is not valid ")
    void find_shouldReturnBadRequestWhenUserIdIsNotValid() {
        // GIVEN
        String invalidUserId = "invalidUserId";

        // WHEN
        ResponseEntity<?> result = userController.findById(invalidUserId);

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Tag("delete")
    @DisplayName("Should delete user when authenticated and user exists")
    void save_shouldDeleteUserWhenAuthenticated() {
        // GIVEN
        Long userId = user1.getId();

        when(userService.findById(userId)).thenReturn(user1);
        when(securityContext.getAuthentication()).thenReturn((Authentication) authentication);
        when(((Authentication) authentication).getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user1.getEmail());

        // WHEN
        ResponseEntity<?> result = userController.save(String.valueOf(userId));

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService).delete(userId);
    }

    @Test
    @Tag("delete")
    @DisplayName("Should return bad request when the user Id is not valid")
    void delete_shouldReturnBadRequestWhenIdIsNotValid() {
        // GIVEN
        String invalidUserId = "invalidUserId";

        // WHEN
        ResponseEntity<?> result = userController.save(invalidUserId);

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Tag("delete")
    @DisplayName("Should returnnot found when the user does not exist")
    void delete_shouldReturnNotFoundWhenUserNotExists() {
        // GIVEN
        Long userId = 88L;
        when(userService.findById(userId)).thenReturn(null);

        // WHEN
        ResponseEntity<?> result = userController.save("" + userId);

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Tag("delete")
    @DisplayName("Should return unauthorized when the user is not authenticated")
    void delete_shouldReturnUnauthorizedWhenUserIsNotAuthenticated() {
        // GIVEN
        Long userId = user1.getId();

        when(userService.findById(userId)).thenReturn(user1);
        when(securityContext.getAuthentication()).thenReturn((Authentication) authentication);
        when(((Authentication) authentication).getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(null);

        // WHEN
        ResponseEntity<?> result = userController.save(String.valueOf(userId));

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(userService, never()).delete(userId);
    }
}
