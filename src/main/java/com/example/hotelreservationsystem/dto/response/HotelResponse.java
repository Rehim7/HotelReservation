package com.example.hotelreservationsystem.dto.response;

import com.example.hotelreservationsystem.model.Room;
import com.example.hotelreservationsystem.model.UserOpinions;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<UserOpinions> userOpinions;
    
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<Room> rooms;
}
