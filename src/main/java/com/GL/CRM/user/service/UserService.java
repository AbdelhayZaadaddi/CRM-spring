package com.GL.CRM.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.GL.CRM.user.entity.User;
import com.GL.CRM.user.repositry.UserRepositry;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepositry userRepository;
    private final PasswordEncoder passwordEncoder;

    public User updateName(User user, String newName) {
        user.setName(newName);
        return userRepository.save(user);
    }

    public void updatePassword(User user, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
