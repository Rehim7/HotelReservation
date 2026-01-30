package com.example.hotelreservationsystem.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "Room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "Room_Prices", nullable = false)
    private Double price;
    @Column(name = "Room_Descriptions")
    private String description;
    @Column(name = "RoomNumber", nullable = false)
    private int roomNumber;
    @Column(name = "Room_Views")
    private String roomView;
    @Column(name = "Room_Stars")
    private Double roomStar = 0.0;
    @Column(name = "Reserved", nullable = false)
    private boolean isReserved = false;
    @Column(nullable = false)
    private RoomType roomType;

    @OneToMany(targetEntity = UserOpinions.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "room_all_user_opinions", joinColumns = @JoinColumn(name = "room_id"), inverseJoinColumns = @JoinColumn(name = "user_opinions_id"))
    private List<UserOpinions> userOpinions;

    @ManyToOne(targetEntity = Hotel.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private Hotel belongingHotel;
}
