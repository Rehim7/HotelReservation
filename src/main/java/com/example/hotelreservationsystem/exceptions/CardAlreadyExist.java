package com.example.hotelreservationsystem.exceptions;

public class CardAlreadyExist extends RuntimeException {
    public CardAlreadyExist(String message) {
        super(message);
    }
}
