package com.example.hotelreservationsystem.dto.response;

import com.example.hotelreservationsystem.model.RoomType;
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

    // Sadə sahələr: lazy proxy-lər seriyalaşarkən xəta verməsin deyə
    private List<String> userOpinionTexts;
    private Long belongingHotelId;
    private String ownerUserEmail;
    private RoomType roomType;
}
