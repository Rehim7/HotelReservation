package com.example.hotelreservationsystem.repository;

import com.example.hotelreservationsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>{
    Optional<User> findByEmail(String email);

    <T> User findByUsername(String username);
}

