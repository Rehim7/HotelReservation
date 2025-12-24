package com.example.hotelreservationsystem.repository;

import com.example.hotelreservationsystem.model.RefreshToken;
import com.example.hotelreservationsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);
}
