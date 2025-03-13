package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    private User user;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @BeforeEach
    void initEach() {
        user = new User(1L, "jdoe@mx.com", "doe", "john", "test", false, null, null);

    }

    @Test
    @DisplayName("Should loadUserByUsername return new UserDetailsImpl")
    void loadUserByUsername_shouldReturnUserDetailsImpl() {
        // GIVEN
        String email = "jdoe@mx.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // WHEN
        UserDetails result = userDetailsServiceImpl.loadUserByUsername(email);

        // THEN
        verify(userRepository).findByEmail(email);
        assertThat(result.getUsername()).isEqualTo(user.getEmail());
        assertThat(result.getPassword()).isEqualTo(user.getPassword());
        
        
    }
}
