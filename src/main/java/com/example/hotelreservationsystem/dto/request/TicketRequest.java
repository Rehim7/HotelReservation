package com.example.hotelreservationsystem.dto.request;

import com.example.hotelreservationsystem.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class TicketRequest {

    @NotBlank
    private String roomNumber;


    @NotBlank
    private Date startDate;
    @NotBlank
    private Date endDate;
}

