package com.aerobins.backend.repository;

import com.aerobins.backend.entity.LoginActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginActivityRepository extends JpaRepository<LoginActivity, Long> {
    List<LoginActivity> findByUsernameOrderByLoginTimeDesc(String username);
}

