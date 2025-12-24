package com.example.hotelreservationsystem.dto.request;

import lombok.Data;

@Data
public class MailRequest {
    String to;
    String subject;
    String body;
}
