package com.GL.CRM;

import com.GL.CRM.user.entity.Role;
import com.GL.CRM.user.entity.User;
import com.GL.CRM.user.repositry.UserRepositry;
import com.GL.CRM.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositry userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("encodedOldPassword")
                .role(Role.USER)
                .build();
    }

    // --- updateName ---

    @Test
    void updateName_shouldUpdateAndReturnUser() {
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateName(user, "Jane Doe");

        assertThat(result.getName()).isEqualTo("Jane Doe");
        verify(userRepository).save(user);
    }

    @Test
    void updateName_shouldPersistNewName() {
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateName(user, "Updated Name");

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    // --- updatePassword ---

    @Test
    void updatePassword_shouldUpdatePassword_whenOldPasswordIsCorrect() {
        when(passwordEncoder.matches("oldPassword123", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword456")).thenReturn("encodedNewPassword");

        userService.updatePassword(user, "oldPassword123", "newPassword456");

        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
        verify(userRepository).save(user);
    }

    @Test
    void updatePassword_shouldThrow_whenOldPasswordIsWrong() {
        when(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updatePassword(user, "wrongPassword", "newPassword456"));

        assertThat(ex.getMessage()).isEqualTo("Old password is incorrect");
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updatePassword_shouldEncodeNewPasswordBeforeSaving() {
        when(passwordEncoder.matches("oldPassword123", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword456")).thenReturn("encodedNewPassword");

        userService.updatePassword(user, "oldPassword123", "newPassword456");

        verify(passwordEncoder).encode("newPassword456");
        verify(userRepository).save(user);
    }
}
