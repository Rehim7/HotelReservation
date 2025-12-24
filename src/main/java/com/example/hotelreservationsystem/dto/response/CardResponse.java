package com.example.hotelreservationsystem.dto.response;

import lombok.Data;

@Data
public class CardResponse {
    private Long id;
    private Long cardNumber;
    private String expirationDate;
    private String cvv;
    private String cardHolderName;
    private Long cardBalance;
}
