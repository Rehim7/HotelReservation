package com.example.hotelreservationsystem.service;

import com.example.banksystem.proto.DecreaseCardBalanceRequest;
import com.example.banksystem.proto.GetCardByIdRequest;
import com.example.hotelreservationsystem.dto.request.TicketRequest;
import com.example.hotelreservationsystem.dto.response.TicketResponse;
import com.example.hotelreservationsystem.exceptions.BalanceIsNotEnough;
//import com.example.hotelreservationsystem.model.Card;
import com.example.hotelreservationsystem.exceptions.RoomReservedException;
import com.example.hotelreservationsystem.model.Room;
import com.example.hotelreservationsystem.model.Ticket;
import com.example.hotelreservationsystem.model.User;
//import com.example.hotelreservationsystem.repository.CardRepository;
import com.example.hotelreservationsystem.repository.RoomRepository;
import com.example.hotelreservationsystem.repository.TicketRepository;
import com.example.hotelreservationsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final CardGrpcClientService cardGrpcClientService;

    private static final String Ticket_Data = "Ticket";

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository, RoomRepository roomRepository, CardGrpcClientService cardGrpcClientService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.cardGrpcClientService = cardGrpcClientService;
    }


    @Cacheable(value = Ticket_Data)
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public TicketResponse getTicketByUserName(String userName) {
        User user =  userRepository.findByEmail(userName)
                                 .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userName));
        Ticket ticket = ticketRepository.getReferenceById(user.getTicketId());
        TicketResponse ticketResponse = new TicketResponse();
        ticketResponse.setId(ticket.getId());
        ticketResponse.setRoomNumber(String.valueOf(ticket.getRoomNumber()));
        ticketResponse.setStartDate(ticket.getStartDate());
        ticketResponse.setEndDate(ticket.getEndDate());
        ticketResponse.setTicketNumber(ticket.getTicketNumber());
        return ticketResponse;
    }


//    @Transactional
//    public TicketResponse buyTicket(Long cardId,  TicketRequest  ticketRequest,Long userId) {
//        Room byRoomNumber = roomRepository.findByRoomNumber(ticketRequest.getRoomNumber());
//        Double price = byRoomNumber.getPrice();
//
//        List<Ticket> byRoomNumber1 = ticketRepository.findByRoomNumber(ticketRequest.getRoomNumber());
//        if (!byRoomNumber1.isEmpty()){
//            throw new RoomReservedException("Room already reserved!");
//        }
//
//        Ticket ticket = new Ticket();
//        ticket.setTicketNumberOnPersist();
//        ticket.setRoomNumber(ticketRequest.getRoomNumber());
//        ticket.setStartDate(ticketRequest.getStartDate());
//        ticket.setEndDate(ticketRequest.getEndDate());
//        ticket.setUserId(userId);
//
//
//        DecreaseCardBalanceRequest decreaseCardBalanceRequest = DecreaseCardBalanceRequest.newBuilder()
//                .setId(cardId)
//                .setAmountToUpdate
//                .
//
//    }





// ========================================================================

//    @Transactional
//    public TicketResponse buyTicket(Long cardId,  TicketRequest  ticketRequest,Long roomNumber) {
//        Card byId = cardRepository.findCardById((cardId));
//        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//        if (byId.getUser() != null && byId.getUser().getEmail().equals(authenticatedUserEmail)) {
//            Room roomByid = roomRepository.findByRoomNumber(roomNumber);
//            Double price = roomByid.getPrice();
//            double v = byId.getCardBalance() - price;
//            if (v < 0) {
//                throw new BalanceIsNotEnough("Balance is less than 0");
//            } else {
//                byId.setCardBalance((long) v);
//                cardRepository.save(byId);
//                Ticket ticket =  new Ticket();
//                ticket.setUser(byId.getUser());
//                ticket.setRoomNumber(roomNumber);
//                ticket.setEndDate(ticketRequest.getEndDate());
//                ticket.setStartDate(ticketRequest.getStartDate());
//                roomByid.setReserved(true);
//                roomRepository.save(roomByid);
//                ticketRepository.save(ticket);
//
//                TicketResponse ticketResponse = new TicketResponse();
//                ticketResponse.setTicketNumber(ticket.getTicketNumber());
//                ticketResponse.setId(ticket.getId());
//                ticketResponse.setRoomNumber(String.valueOf(ticket.getRoomNumber()));
//                ticketResponse.setStartDate(ticketRequest.getStartDate());
//                ticketResponse.setEndDate(ticketRequest.getEndDate());
//                return ticketResponse;
//            }
//        } else {
//            throw new UsernameNotFoundException("User and Card do not match or user not found.");
//        }
//    }
//
//    @Transactional
//    @CacheEvict(value = Ticket_Data,allEntries = true)
//    public void cancelTicket(Long cardId) {
//        Card byId = cardRepository.findCardById((cardId));
//        String authenticatedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//        User authenticatedUser = userRepository.findByEmail(authenticatedUserEmail)
//                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found."));
//        if (byId.getUser() != null && byId.getUser().equals(authenticatedUser)) {
//            Ticket ticket = authenticatedUser.getTicket();
//            if (ticket != null) {
//                Long roomNumber = ticket.getRoomNumber();
//                Room roomByRoomNumber = roomRepository.findByRoomNumber(roomNumber);
//
//                byId.setCardBalance((long) (byId.getCardBalance() + roomByRoomNumber.getPrice()));
//                ticketRepository.delete(ticket);
//                roomByRoomNumber.setReserved(false);
//                roomRepository.save(roomByRoomNumber);
//            } else{
//                throw new NullPointerException("Ticket not found for the authenticated user.");
//            }
//
//        } else {
//            throw new UsernameNotFoundException("User and Card do not match or user not found.");
//        }
//    }

}
