package com.example.hotelreservationsystem.dto.request;

import com.example.hotelreservationsystem.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class TicketRequest {

    @NotNull
    private Long roomNumber;


    @NotNull
    private Date startDate;
    @NotNull
    private Date endDate;
}

