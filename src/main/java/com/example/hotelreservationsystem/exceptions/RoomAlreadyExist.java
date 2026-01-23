package com.example.hotelreservationsystem.exceptions;

public class RoomAlreadyExist extends RuntimeException {
    public RoomAlreadyExist(String message) {
        super(message);
    }
}
