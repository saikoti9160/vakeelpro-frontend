package com.digiworld.vakeelpro.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digiworld.vakeelpro.entities.ResetToken;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
    ResetToken findByToken(String token);
}