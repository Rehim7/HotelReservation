package com.example.hotelreservationsystem.exceptions;

public class HotelAlreadyExist extends RuntimeException {
    public HotelAlreadyExist(String message) {
        super(message);
    }
}
