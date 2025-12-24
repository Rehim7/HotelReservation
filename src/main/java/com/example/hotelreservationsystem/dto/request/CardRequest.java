package com.example.hotelreservationsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CardRequest {
    @NotBlank
    @Size(min = 8, max = 12)
    private Long cardNumber;
    @NotBlank
    private String expirationDate;
    @NotBlank
    @Size(min = 2, max = 4)
    private String cvv;
    @NotBlank
    private String cardHolderName;
}
