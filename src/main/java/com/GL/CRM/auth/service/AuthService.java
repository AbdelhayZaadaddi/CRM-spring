package com.GL.CRM.auth.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.GL.CRM.security.JwtService;
import com.GL.CRM.user.entity.Role;
import com.GL.CRM.user.entity.User;
import com.GL.CRM.user.repositry.UserRepositry;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepositry repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public Map<String, String> register(User request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("email", user.getEmail());

        return response;
    }

    public Map<String, String> authenticate(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        var user = repository.findByEmail(email).orElseThrow();

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "User authenticated successfully");
        response.put("token", jwtService.generateToken(user));
        return response;
    }
}