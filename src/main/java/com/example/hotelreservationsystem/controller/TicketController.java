package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.dto.request.TicketRequest;
import com.example.hotelreservationsystem.dto.response.TicketResponse;
import com.example.hotelreservationsystem.exceptions.BalanceIsNotEnough;
import com.example.hotelreservationsystem.model.Card;
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


    @PostMapping("/buyTicket/{cardId}/{userHolderName}/{roomNumber}")
    @PreAuthorize("hasAnyRole( 'ROLE_USER','ROLE_ADMIN','ROLE_HOTELOWNER')")
    public TicketResponse buyTicket(@PathVariable Long cardId, @PathVariable String userHolderName, @PathVariable int roomNumber,@RequestBody TicketRequest ticketRequest) {
        return ticketService.buyTicket(cardId,userHolderName,ticketRequest,roomNumber);
    }


    @GetMapping("/getAllTickets")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_HOTELOWNER')")
    public List<Ticket> getAll(){
        return ticketService.getAllTickets();
    }

    @GetMapping("/getTicketByUserName/{username}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN','ROLE_HOTELOWNER')")
    public TicketResponse getTicket(@PathVariable String username){
        return ticketService.getTicketByUserName(username);
    }

    @PostMapping("/cancelTicket/{cardId}/{userHolderName}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public void cancelTicket(@PathVariable Long cardId,@PathVariable String userHolderName) {
        ticketService.cancelTicket(cardId,userHolderName);
    }




}
