package com.example.hotelreservationsystem.dto.response;

import com.example.hotelreservationsystem.model.Hotel;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.model.UserOpinions;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class RoomResponse {
    private Long id;
    private double price;
    private String description;
    private int roomNumber;
    private String roomView;
    private double roomStar;
    private boolean isReserved;
    private List<UserOpinions> userOpinions;
    private Hotel belongingHotel;
    private User ownerUser;
}
