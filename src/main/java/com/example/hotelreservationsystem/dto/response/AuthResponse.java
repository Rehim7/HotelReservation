package com.example.hotelreservationsystem.dto.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private String token;

    public AuthResponse(String accessToken, String token) {
        this.accessToken = accessToken;
        this.token = token;
    }
}


