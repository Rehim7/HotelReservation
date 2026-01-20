package com.example.hotelreservationsystem.model;

import jakarta.persistence.*;
import jakarta.persistence.PostPersist;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;

@Entity
@Data
@Table(name = "Tickets")
public class Ticket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private Long ticketNumber;
    private Long roomNumber;
    private Date startDate;
    private Date endDate;
    private Long userId;

    @PostPersist
    public void setTicketNumberOnPersist() {
        if (this.ticketNumber == null) {
            this.ticketNumber = this.id;
        }
    }



}
