package com.example.hotelreservationsystem.exceptions;

public class BalanceIsNotEnough extends RuntimeException {
    public BalanceIsNotEnough(String message) {
        super(message);
    }
}
