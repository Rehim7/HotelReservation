package com.example.hotelreservationsystem.dto.request;

import com.example.hotelreservationsystem.model.Room;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.model.UserOpinions;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.awt.*;
import java.util.List;

@Data
public class HotelRequest {
    @NotBlank(message = "Hotel should have a name")
    private String hotelName;
    @NotBlank(message = "Hotel address needed")
    private String hotelAddress;
    private String hotelDescription;
    @NotBlank(message = "Hotel should have Hotel view information for users")
    private String hotelImageUrl;
    @NotBlank(message = "Hotel needs at least one room")
    private List<Room> rooms;
    @NotBlank(message = "HotelOwner required")
    private User hotelOwner;
    private MailRequest mailRequest;
}
