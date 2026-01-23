package com.example.hotelreservationsystem.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class HotelResponse {
    private Long id;
    private String hotelName;
    private String hotelAddress;
    private String hotelDescription;
    private double hotelStars;
    private String hotelImageUrl;

    private List<String> userOpinions;
    private List<RoomResponse> rooms;
    private String hotelOwner;
}
