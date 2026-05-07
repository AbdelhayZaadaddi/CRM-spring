package com.GL.CRM.user.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GL.CRM.user.dto.UpdateNameRequest;
import com.GL.CRM.user.dto.UpdatePasswordRequest;
import com.GL.CRM.user.dto.UserResponse;
import com.GL.CRM.user.entity.User;
import com.GL.CRM.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()));
    }

    @PatchMapping("/me/name")
    public ResponseEntity<UserResponse> updateName(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateNameRequest request) {
        User updated = userService.updateName(user, request.getName());
        return ResponseEntity.ok(new UserResponse(
                updated.getId(),
                updated.getName(),
                updated.getEmail(),
                updated.getRole().name()));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Map<String, String>> updatePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(user, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Logged out successfully. Please delete your token on the client side."));
    }
}
