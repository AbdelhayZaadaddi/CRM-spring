package com.GL.CRM.user.repositry;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GL.CRM.user.entity.User;

public interface UserRepositry extends JpaRepository<User, Long> {

}
