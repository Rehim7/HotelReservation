package com.example.hotelreservationsystem.exceptions;

public class TicketDoesntExist extends RuntimeException {
    public TicketDoesntExist(String message) {
        super(message);
    }
}
