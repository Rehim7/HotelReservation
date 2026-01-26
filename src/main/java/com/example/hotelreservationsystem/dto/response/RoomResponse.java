package com.example.hotelreservationsystem.dto.response;

import com.example.hotelreservationsystem.model.Hotel;
import com.example.hotelreservationsystem.model.RoomType;
import com.example.hotelreservationsystem.model.UserOpinions;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class RoomResponse {
    private Long id;
    private Double price;
    private String description;
    private int roomNumber;
    private String roomView;
    private Double roomStar = 0.0;
    private boolean isReserved = false;
    private RoomType roomType;
    private List<String> userOpinions;
    private String belongingHotel;
}
