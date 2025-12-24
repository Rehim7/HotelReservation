package com.example.hotelreservationsystem.exceptions;

public class CardNotFound extends RuntimeException {
    public CardNotFound(String message) {
        super(message);
    }
}
