package com.example.hotelreservationsystem.dto.response;

import com.example.hotelreservationsystem.model.Room;
import com.example.hotelreservationsystem.model.UserOpinions;
import jakarta.persistence.*;
import lombok.Data;

import java.awt.*;
import java.util.List;
@Data
public class HotelResponse {
    private Long id;
    private String hotelName;
    private String hotelAddress;
    private String hotelDescription;
    private double hotelStars;
    private String hotelImageUrl;
    private List<UserOpinions> userOpinions;
    private List<Room> rooms;
}
