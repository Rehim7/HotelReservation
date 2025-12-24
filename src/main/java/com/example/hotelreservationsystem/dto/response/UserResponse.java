package com.example.hotelreservationsystem.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String username;
    private String email;
    private String password;
    private String userRole;
}
