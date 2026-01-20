package com.example.hotelreservationsystem.dto.request;

import com.example.hotelreservationsystem.model.Room;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.model.UserOpinions;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "Hotel owner email is required")
    private String hotelOwner;

    private List<Room> rooms;

    private MailRequest mailRequest;
}