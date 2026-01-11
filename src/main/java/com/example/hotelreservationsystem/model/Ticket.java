package com.example.hotelreservationsystem.model;

import jakarta.persistence.*;
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
    private Long ticketNumber = id;
    private int roomNumber;
    private Date startDate;
    private Date endDate;

    @OneToOne(targetEntity = User.class,cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
    private User user;


}
