package com.example.hotelreservationsystem.dto.request;

import com.example.hotelreservationsystem.model.Hotel;
import com.example.hotelreservationsystem.model.Room;
import com.example.hotelreservationsystem.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserOpininonRequest {
    @NotBlank @Size(min = 1, max = 5)
    private double rating;
    private String userOpinions;
    @NotBlank
    private String user;
}
