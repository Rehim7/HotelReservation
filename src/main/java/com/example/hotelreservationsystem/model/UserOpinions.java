package com.example.hotelreservationsystem.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class UserOpinions {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double rating;
    private String userOpinions;

    @ManyToOne(targetEntity = User.class,cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private User user;
}
