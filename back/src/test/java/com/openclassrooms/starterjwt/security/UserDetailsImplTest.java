package com.openclassrooms.starterjwt.security;

import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

@ExtendWith(MockitoExtension.class)
public class UserDetailsImplTest {

    private UserDetailsImpl userDetailsImpl;

    @BeforeEach
    void initEach() {
        userDetailsImpl = UserDetailsImpl.builder().id(1L).username("jdoe@mx.com").firstName("John").lastName("Doe")
                .admin(false).password("test123").build();

    }

    @Test
    @DisplayName("Should getAuthorities return new Empty Set of GrantAuthority type")
    void getAuthorities_shouldReturnEmptyHashSetOfGrantAuthority() {
        // GIVEN

        // WHEN
       
        Collection<? extends GrantedAuthority> autorities = userDetailsImpl.getAuthorities();

        // THEN
        assertThat(autorities).isInstanceOf(Set.class);
        assertThat(autorities).isEmpty();

    }

    @Test
    @DisplayName("Should isAccountNonExpired return True")
    void isAccountNonExpired_shouldReturnTrue() {
        assertThat(userDetailsImpl.isAccountNonExpired()).isTrue();
    }

    @Test
    @DisplayName("Should isAccountNonLocked return True")
    void isAccountNonLocked_shouldReturnTrue() {
        assertThat(userDetailsImpl.isAccountNonLocked()).isTrue();
    }

    @Test
    @DisplayName("Should isCredentialsNonExpired return True")
    void isCredentialsNonExpired_shouldReturnTrue() {
        assertThat(userDetailsImpl.isCredentialsNonExpired()).isTrue();
    }

    @Test
    @DisplayName("Should isEnabled return True")
    void isEnabled_shouldReturnTrue() {
        assertThat(userDetailsImpl.isEnabled()).isTrue();
    }

    @Test
    @Tag("equals")
    @DisplayName("Should equals return true when parameter object is same reference")
    void equals_shouldReturnTrueWhenObjectIsSameReference() {
        // GIVEN
        Object expectedObject = userDetailsImpl;

        // WHEN
        boolean result = userDetailsImpl.equals(expectedObject);

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    @Tag("equals")
    @DisplayName("Should equals return false when parameter object is null")
    void equals_shouldReturnFalseWhenObjectIsNull() {
        // GIVEN
        Object nullObject = null;

        // WHEN
        boolean result = userDetailsImpl.equals(nullObject);

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    @Tag("equals")
    @DisplayName("Should equals return false when parameter object is null")
    void equals_shouldReturnFalseWhenObjectIsDifferentClass() {
        // GIVEN
        String otherObject = "Not a UserDetailsImpl";

        // WHEN
        boolean result = userDetailsImpl.equals(otherObject);

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    @Tag("equals")
    @DisplayName("Should equals return false when id is different")
    void equals_shouldReturnFalseWhenIdIsDifferent() {
        // GIVEN
        UserDetailsImpl otherUserDetailImpl = new UserDetailsImpl(2L, "jdoe@mx.com", "John", "Doe", false, "test123");

        // WHEN
        boolean result = userDetailsImpl.equals(otherUserDetailImpl);

        // THEN
        assertThat(result).isFalse();
    }

    @Test
    @Tag("equals")
    @DisplayName("Should equals return false when id is same")
    void equals_shouldReturnFalseWhenIdIsSame() {
        // GIVEN
        UserDetailsImpl otherUserDetailImpl = new UserDetailsImpl(1L, "jsmith@mx.com", "John", "Smith", false,
                "test123");

        // WHEN
        boolean result = userDetailsImpl.equals(otherUserDetailImpl);

        // THEN
        assertThat(result).isTrue();
    }

}
