package com.example.hotelreservationsystem.service;

import com.example.hotelreservationsystem.dto.request.TicketRequest;
import com.example.hotelreservationsystem.dto.response.TicketResponse;
import com.example.hotelreservationsystem.exceptions.BalanceIsNotEnough;
import com.example.hotelreservationsystem.model.Card;
import com.example.hotelreservationsystem.model.Room;
import com.example.hotelreservationsystem.model.Ticket;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.repository.CardRepository;
import com.example.hotelreservationsystem.repository.RoomRepository;
import com.example.hotelreservationsystem.repository.TicketRepository;
import com.example.hotelreservationsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardService cardService;
    private final RoomRepository roomRepository;

    private static final String Ticket_Data = "Ticket";

    public TicketService(TicketRepository ticketRepository, CardRepository cardRepository, UserRepository userRepository, CardService cardService, RoomRepository roomRepository) {
        this.ticketRepository = ticketRepository;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.cardService = cardService;
        this.roomRepository = roomRepository;
    }


    @Transactional
    public TicketResponse buyTicket(Long cardId, String userHolderName, TicketRequest  ticketRequest,int roomNumber) {
        Card byId = cardRepository.findCardById((cardId));
        if (byId.getCardHolderName().equals(userHolderName)) {
            Room roomByid = roomRepository.findByRoomNumber(roomNumber);
            Double price = roomByid.getPrice();
            double v = byId.getCardBalance() - price;
            if (v < 0) {
                throw new BalanceIsNotEnough("Balance is less than 0");
            } else {
                byId.setCardBalance((long) v);
                cardRepository.save(byId);
                Ticket ticket =  new Ticket();
                ticket.setUser(byId.getUser());
                ticket.setRoomNumber(roomNumber);
                ticket.setEndDate(ticketRequest.getEndDate());
                ticket.setStartDate(ticketRequest.getStartDate());
                roomByid.setReserved(true);
                roomRepository.save(roomByid);
                ticketRepository.save(ticket);

                TicketResponse ticketResponse = new TicketResponse();
                ticketResponse.setTicketNumber(ticket.getTicketNumber());
                ticketResponse.setId(ticket.getId());
                ticketResponse.setRoomNumber(String.valueOf(ticket.getRoomNumber()));
                ticketResponse.setStartDate(ticketRequest.getStartDate());
                ticketResponse.setEndDate(ticketRequest.getEndDate());
                return ticketResponse;
            }
        } else {
            try {
                throw new Exception("User and Card doesnt match");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Transactional
    @CacheEvict(value = Ticket_Data,allEntries = true)
    public void cancelTicket(Long cardId, String userHolderName) {
        Card byId = cardRepository.findCardById((cardId));
        User userByUserName = userRepository.findUserByUsername((userHolderName));
        if (byId.getUser().equals(userByUserName)) {
            Ticket ticket = userByUserName.getTicket();
            int roomNumber = ticket.getRoomNumber();
            Room roomByRoomNumber = roomRepository.findByRoomNumber(roomNumber);

            byId.setCardBalance((long) (byId.getCardBalance() + roomByRoomNumber.getPrice()));
            ticketRepository.delete(ticket);
            roomByRoomNumber.setReserved(false);
            roomRepository.save(roomByRoomNumber);
        } else {
            try {
                throw new Exception("User and Card doesnt match");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Cacheable(value = Ticket_Data)
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public TicketResponse getTicketByUserName(String userName) {
        User user =  userRepository.findUserByUsername((userName));
        Ticket ticket = user.getTicket();
        TicketResponse ticketResponse = new TicketResponse();
        ticketResponse.setId(ticket.getId());
        ticketResponse.setRoomNumber(String.valueOf(ticket.getRoomNumber()));
        ticketResponse.setStartDate(ticket.getStartDate());
        ticketResponse.setEndDate(ticket.getEndDate());
        ticketResponse.setTicketNumber(ticket.getTicketNumber());
        return ticketResponse;
    }



}
