package com.example.hotelreservationsystem.dto.response;

import com.example.hotelreservationsystem.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.util.Date;

@Data
public class TicketResponse {
    private Long id;
    private Long ticketNumber;
    private String roomNumber;
    private Date startDate;
    private Date endDate;


}
