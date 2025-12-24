package com.example.hotelreservationsystem.exceptions;

public class TokenRefreshException extends RuntimeException {
    public TokenRefreshException(String message,String s) {
        super(message);
    }
}
