package com.example.hotelreservationsystem.dto.request;

import com.example.hotelreservationsystem.model.Hotel;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.model.UserOpinions;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    private boolean isReserved = false;
    @NotBlank(message = "Room should be in a hotel")
    private String belongingHotel;
}
