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
    @Column(name = "Room_Stars")
    private Double roomStar = 0.0;
    @Column(name = "Reserved",nullable = false)
    private boolean isReserved = false;
    @Column(nullable = false)
    private RoomType roomType;


    @OneToMany(targetEntity = UserOpinions.class, cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<UserOpinions> userOpinions;

    @ManyToOne(targetEntity = Hotel.class,cascade = CascadeType.MERGE,fetch = FetchType.LAZY)
    private Hotel belongingHotel;

    @ManyToOne(targetEntity = User.class,cascade = CascadeType.PERSIST,fetch = FetchType.LAZY)
    private User ownerUser;

}
