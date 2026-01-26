package com.example.hotelreservationsystem.dto.request;

import com.example.hotelreservationsystem.model.Hotel;
import com.example.hotelreservationsystem.model.RoomType;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.model.UserOpinions;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RoomRequest {
    @NotBlank(message = "Room should have price")
    private double price;
    private String description;
    @NotBlank(message = "Users need a room number to find room")
    private int roomNumber;
    @NotBlank(message = "Room view required for users")
    private String roomView;
    @NotNull
    private Long belongingHotelId;
    @NotBlank
    private RoomType roomType;
}
