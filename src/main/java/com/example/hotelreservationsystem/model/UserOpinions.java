package com.example.hotelreservationsystem.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "all_user_opinions")
public class UserOpinions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double rating;
    private String userOpinions;

    private Long userId;
}
