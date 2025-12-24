package com.example.hotelreservationsystem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "Room")
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "Room_Prices",nullable = false)
    private Double price;
    @Column(name = "Room_Descriptions")
    private String description;
    @Column(name = "RoomNumber",nullable = false)
    private int roomNumber;
    @Column(name = "Room_Views")
    private String roomView;
    @Column(name = "Room_Stars",nullable = false)
    private Double roomStar;
    @Column(name = "Reserved",nullable = false)
    private boolean isReserved;


    @OneToMany(targetEntity = UserOpinions.class, cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<UserOpinions> userOpinions;

    @ManyToOne(targetEntity = Hotel.class,cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private Hotel belongingHotel;

    @ManyToOne(targetEntity = User.class,cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private User ownerUser;

}
