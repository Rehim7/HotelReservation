package com.example.hotelreservationsystem.repository;

import com.example.hotelreservationsystem.model.Room;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room,Long> {
    List<Room> findByIsReserved(boolean isReserved);

    Optional<Room> getRoomByid(Long id);

    List<Room> getRoomByRoomNumber(int roomNumber);

    List<Room> findRoomById(Long id);

    List<Room> findRoomByRoomNumber(int roomNumber);

    Room findByRoomNumber(@NotBlank(message = "Users need a room number to find room") int roomNumber);
}

