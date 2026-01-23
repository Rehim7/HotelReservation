package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.dto.request.TicketRequest;
import com.example.hotelreservationsystem.dto.response.TicketResponse;
import com.example.hotelreservationsystem.model.Ticket;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.repository.UserRepository;
import com.example.hotelreservationsystem.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/hotelReservationSystem/ticket")
public class TicketController {
    private final TicketService ticketService;
    private final UserRepository userRepository;

    public TicketController(TicketService ticketService, UserRepository userRepository) {
        this.ticketService = ticketService;
        this.userRepository = userRepository;
    }

    @PostMapping("/buy/{cardId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TicketResponse> buyTicket(@PathVariable Long cardId, @Valid @RequestBody TicketRequest ticketRequest) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found with email: " + currentUserEmail));

        TicketResponse ticketResponse = ticketService.buyTicket(cardId, ticketRequest, currentUser.getId());
        return ResponseEntity.ok(ticketResponse);
    }

    @PostMapping("/cancel/{cardId}/{ticketId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> cancelTicket(@PathVariable Long cardId, @PathVariable Long ticketId) {
        String message = ticketService.cancelTicket(cardId, ticketId);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/getAllTickets")
    @PreAuthorize("isAuthenticated()")
    public List<Ticket> getAll(){
        return ticketService.getAllTickets();
    }

    @GetMapping("/getTicketByUserName/{username}")
    @PreAuthorize("isAuthenticated()")
    public TicketResponse getTicket(@PathVariable String username){
        return ticketService.getTicketByUserName(username);
    }
}
