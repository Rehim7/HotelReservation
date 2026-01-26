package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.dto.request.TicketRequest;
import com.example.hotelreservationsystem.dto.response.TicketResponse;
import com.example.hotelreservationsystem.model.Ticket;
import com.example.hotelreservationsystem.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/hotelReservationSystem/ticket")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/buy")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TicketResponse> buyTicket(
            @Valid @RequestBody TicketRequest ticketRequest,
            @RequestHeader("Authorization") String authHeader) {
        // Token authHeader olaraq servisə ötürülür, id-ni servis çıxarır
        return ResponseEntity.ok(ticketService.buyTicket(ticketRequest, authHeader));
    }

    @PostMapping("/cancel/{ticketId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> cancelTicket(
            @PathVariable Long ticketId,
            @RequestHeader("Authorization") String authHeader) {
        String message = ticketService.cancelTicket(ticketId, authHeader);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/getAllTickets")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Ticket>> getAll() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/getTicketByUserName/{username}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable String username) {
        return ResponseEntity.ok(ticketService.getTicketByUserName(username));
    }
}