package com.example.hotelreservationsystem.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @Email(message = "Email should be in a valid format")
    private String email;
    @Size(min = 4, max = 16,message = "Password lenght should be 4-16")
    private String password;
}
