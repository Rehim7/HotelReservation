package com.example.hotelreservationsystem.repository;

import com.example.hotelreservationsystem.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel,Long> {
    Hotel findByHotelName(String hotelName);

    Hotel getHotelById(Long id);
}
