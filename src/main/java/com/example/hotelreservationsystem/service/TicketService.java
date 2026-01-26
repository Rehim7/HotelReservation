package com.example.hotelreservationsystem.service;

import com.example.banksystem.proto.*;
import com.example.hotelreservationsystem.dto.request.TicketRequest;
import com.example.hotelreservationsystem.dto.response.TicketResponse;
import com.example.hotelreservationsystem.exceptions.*;
import com.example.hotelreservationsystem.model.Room;
import com.example.hotelreservationsystem.model.Ticket;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.repository.RoomRepository;
import com.example.hotelreservationsystem.repository.TicketRepository;
import com.example.hotelreservationsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final CardGrpcClientService cardGrpcClientService;
    private final MailService mailService;
    private final JwtService jwtService;

    private static final String Ticket_Data = "Ticket";

    public TicketService(TicketRepository ticketRepository,
                         UserRepository userRepository,
                         RoomRepository roomRepository,
                         CardGrpcClientService cardGrpcClientService,
                         MailService mailService,
                         JwtService jwtService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.cardGrpcClientService = cardGrpcClientService;
        this.mailService = mailService;
        this.jwtService = jwtService;
    }

    @Cacheable(value = Ticket_Data)
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public TicketResponse getTicketByUserName(String userName) {
        User user = userRepository.findByEmail(userName)
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

    @Transactional
    public TicketResponse buyTicket(TicketRequest ticketRequest, String authHeader) {
        String token = authHeader.substring(7);
        Long userId = jwtService.extractUserId(token);
        String userEmail = jwtService.extractUsername(token);

        Room room = roomRepository.findByRoomNumber(Math.toIntExact(ticketRequest.getRoomNumber()))
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Otaq tapılmadı"));

        if (room.isReserved()) {
            throw new RoomReservedException("Bu otaq artıq rezerv olunub!");
        }

        GetCardByUserIdRequest cardReq = GetCardByUserIdRequest.newBuilder()
                .setUserId(userId)
                .build();
        CardResponse userCard = cardGrpcClientService.getCardByUserId(cardReq);

        DecreaseCardBalanceRequest decreaseReq = DecreaseCardBalanceRequest.newBuilder()
                .setId(userCard.getId())
                .setCardNumber(userCard.getCardNumber())
                .setAmountToUpdate(room.getPrice())
                .build();
        cardGrpcClientService.decreaseCardBalance(decreaseReq);

        Ticket ticket = new Ticket();
        ticket.setTicketNumberOnPersist();
        ticket.setRoomNumber(ticketRequest.getRoomNumber());
        ticket.setStartDate(ticketRequest.getStartDate());
        ticket.setEndDate(ticketRequest.getEndDate());
        ticket.setUserId(userId);
        ticketRepository.save(ticket);

        room.setReserved(true);
        roomRepository.save(room);

        sendConfirmationMail(userEmail, ticketRequest, room.getRoomNumber());

        return mapToResponse(ticket);
    }

    @Transactional
    public String cancelTicket(Long ticketId, String authHeader) {
        String token = authHeader.substring(7);
        Long userId = jwtService.extractUserId(token);
        String userEmail = jwtService.extractUsername(token);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketDoesntExist("Ticket doesn't exist"));

        if (!ticket.getUserId().equals(userId)) {
            throw new RuntimeException("Siz ancaq öz biletinizi ləğv edə bilərsiniz!");
        }

        Room room = roomRepository.findByRoomNumber(Math.toIntExact(ticket.getRoomNumber()))
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Otaq tapılmadı"));

        GetCardByUserIdRequest cardReq = GetCardByUserIdRequest.newBuilder()
                .setUserId(userId)
                .build();
        CardResponse userCard = cardGrpcClientService.getCardByUserId(cardReq);

        IncreaseCardBalanceRequest increaseReq = IncreaseCardBalanceRequest.newBuilder()
                .setId(userCard.getId())
                .setCardNumber(userCard.getCardNumber())
                .setAmountToUpdate(room.getPrice())
                .build();
        cardGrpcClientService.increaseCardBalance(increaseReq);

        ticketRepository.delete(ticket);
        room.setReserved(false);
        roomRepository.save(room);

        sendCancellationMail(userEmail, room.getRoomNumber());

        return "Bilet ləğv edildi və məbləğ geri qaytarıldı.";
    }

    private void sendConfirmationMail(String email, TicketRequest request, Integer roomNum) {
        String subject = "Rezervasiya Təsdiqi";
        String body = String.format("Hörmətli müştəri, %d nömrəli otaq üçün rezervasiyanız tamamlandı. Tarix: %s - %s",
                roomNum, request.getStartDate(), request.getEndDate());
        mailService.sendMail(email, subject, body);
    }

    private void sendCancellationMail(String email, Integer roomNum) {
        String subject = "Rezervasiya Ləğvi";
        String body = String.format("Hörmətli müştəri, %d nömrəli otaq üçün rezervasiyanız ləğv edildi və ödəniş qaytarıldı.", roomNum);
        mailService.sendMail(email, subject, body);
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        TicketResponse res = new TicketResponse();
        res.setId(ticket.getId());
        res.setTicketNumber(ticket.getTicketNumber());
        res.setRoomNumber(String.valueOf(ticket.getRoomNumber()));
        res.setStartDate(ticket.getStartDate());
        res.setEndDate(ticket.getEndDate());
        return res;
    }
}