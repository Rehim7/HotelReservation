//package com.example.hotelreservationsystem.model;
//
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.util.LinkedHashSet;
//
//@Entity
//@Data
//
//@Table(name = "Cards")
//public class Card {
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String cardNumber;
//    private String expirationDate;
//    private String cvv;
//    private String cardHolderName;
//    private Long cardBalance;
//
//
//    @OneToOne(targetEntity = User.class,cascade = CascadeType.PERSIST,fetch = FetchType.EAGER)
//    private User user;
//}
