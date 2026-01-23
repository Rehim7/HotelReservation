package com.example.hotelreservationsystem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "Hotel")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private User hotelOwner;

    @OneToMany(targetEntity = UserOpinions.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserOpinions> userOpinions;

    @OneToMany(mappedBy = "belongingHotel", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();

    // Helper methods for bidirectional relationship
    public void addRoom(Room room) {
        if (rooms == null) {
            rooms = new ArrayList<>();
        }
        rooms.add(room);
        room.setBelongingHotel(this);
    }

    public void removeRoom(Room room) {
        if (rooms != null) {
            rooms.remove(room);
            room.setBelongingHotel(null);
        }
    }

}
