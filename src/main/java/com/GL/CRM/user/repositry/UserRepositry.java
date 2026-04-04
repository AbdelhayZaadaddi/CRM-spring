package com.GL.CRM.user.repositry;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GL.CRM.user.entity.User;

public interface UserRepositry extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
