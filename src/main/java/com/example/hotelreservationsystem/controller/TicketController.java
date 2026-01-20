package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.dto.request.TicketRequest;
import com.example.hotelreservationsystem.dto.response.TicketResponse;
import com.example.hotelreservationsystem.exceptions.BalanceIsNotEnough;
import com.example.hotelreservationsystem.model.Room;
import com.example.hotelreservationsystem.model.Ticket;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.service.TicketService;
import jakarta.transaction.Transactional;
import org.hibernate.dialect.LobMergeStrategy;
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


//    @PostMapping("/buyTicket/{cardId}/{roomNumber}")
//    @PreAuthorize("isAuthenticated()")
//    public TicketResponse buyTicket(@PathVariable Long cardId, @PathVariable Long roomNumber,@RequestBody TicketRequest ticketRequest) {
//        return ticketService.buyTicket(cardId,ticketRequest,roomNumber);
//    }


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

//    @PostMapping("/cancelTicket/{cardId}")
//    @PreAuthorize("isAuthenticated()")
//    public void cancelTicket(@PathVariable Long cardId) {
//        ticketService.cancelTicket(cardId);
//    }




}
