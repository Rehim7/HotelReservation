package com.example.hotelreservationsystem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.awt.*;
import java.util.List;

@Entity
@Data
@Table(name = "Hotel")
public class Hotel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "Hotel_Name", nullable = false)
    private String hotelName;
    @Column(name = "Hotel_Address", nullable = false)
    private String hotelAddress;
    @Column(name = "Hotel_Description")
    private String hotelDescription;
    @Column(name = "Hotel_Stars")
    private double hotelStars;
    @Column(length = 500)
    private String hotelImageUrl;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User hotelOwner;

    @OneToMany(targetEntity = UserOpinions.class,cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<UserOpinions> userOpinions;

    @OneToMany(targetEntity = Room.class,cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Room> rooms;


}
