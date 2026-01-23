package com.example.hotelreservationsystem.service;

import com.example.banksystem.proto.CardResponse;
import com.example.banksystem.proto.DecreaseCardBalanceRequest;
import com.example.banksystem.proto.GetCardByIdRequest;
import com.example.banksystem.proto.IncreaseCardBalanceRequest;
import com.example.hotelreservationsystem.dto.request.TicketRequest;
import com.example.hotelreservationsystem.dto.response.TicketResponse;
import com.example.hotelreservationsystem.exceptions.*;
//import com.example.hotelreservationsystem.model.Card;
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
import java.util.Optional;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final CardGrpcClientService cardGrpcClientService;
    private final MailService mailService;

    private static final String Ticket_Data = "Ticket";

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository, RoomRepository roomRepository, CardGrpcClientService cardGrpcClientService, MailService mailService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.cardGrpcClientService = cardGrpcClientService;
        this.mailService = mailService;
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


    @Transactional
    public TicketResponse buyTicket(Long cardId,  TicketRequest  ticketRequest,Long userId) {
        List<Room> byRoomNumber =  roomRepository.findByRoomNumber(Math.toIntExact(ticketRequest.getRoomNumber()));
        Double price = byRoomNumber.get(0).getPrice();

        List<Ticket> byRoomNumber1 = ticketRepository.findByRoomNumber(ticketRequest.getRoomNumber());
        if (!byRoomNumber1.isEmpty()){
            throw new RoomReservedException("Room already reserved!");
        }

        Ticket ticket = new Ticket();
        ticket.setTicketNumberOnPersist();
        ticket.setRoomNumber(ticketRequest.getRoomNumber());
        ticket.setStartDate(ticketRequest.getStartDate());
        ticket.setEndDate(ticketRequest.getEndDate());
        ticket.setUserId(userId);

        GetCardByIdRequest getCardByIdRequest = GetCardByIdRequest.newBuilder()
                .setId(cardId)
                .build();
        CardResponse cardById = cardGrpcClientService.getCardById(getCardByIdRequest);


        DecreaseCardBalanceRequest decreaseCardBalanceRequest = DecreaseCardBalanceRequest.newBuilder()
                .setId(cardId)
                .setAmountToUpdate(price)
                .setCardNumber(cardById.getCardNumber())
                .build();
        cardGrpcClientService.decreaseCardBalance(decreaseCardBalanceRequest);
        userRepository.findById(userId).ifPresent(user -> {
            String subject = "Rezervasiya Təsdiqi";
            String body = String.format("Hörmətli %s,\n\n%d nömrəli otaq üçün rezervasiyanız uğurla tamamlandı. \nBaşlama tarixi: %s\nBitmə tarixi: %s",
                    user.getUsername(),
                    ticketRequest.getRoomNumber(),
                    ticketRequest.getStartDate().toString(),
                    ticketRequest.getEndDate().toString());
            mailService.sendMail(user.getEmail(), subject, body);
        });

        TicketResponse ticketResponse = new TicketResponse();
        ticketResponse.setId(ticket.getId());
        ticketResponse.setRoomNumber(String.valueOf(ticket.getRoomNumber()));
        ticketResponse.setStartDate(ticket.getStartDate());
        ticketResponse.setEndDate(ticket.getEndDate());
        ticketResponse.setTicketNumber(ticket.getTicketNumber());
        byRoomNumber.get(0).setReserved(true);
        return ticketResponse;
    }

    @Transactional
    public String cancelTicket(Long cardId,Long ticketId){
        try {
            Optional<Ticket> byId = ticketRepository.findById(ticketId);
            if (byId.isEmpty()) {
                throw new TicketDoesntExist("Ticket doesn't exist");
            }
            Ticket ticket = byId.get();
            Long roomNumber = ticket.getRoomNumber();
            Room byRoomNumber = (Room) roomRepository.findByRoomNumber(Math.toIntExact(roomNumber));
            Double price = byRoomNumber.getPrice();
            GetCardByIdRequest getCardByIdRequest = GetCardByIdRequest.newBuilder()
                    .setId(cardId)
                    .build();
            CardResponse cardById = cardGrpcClientService.getCardById(getCardByIdRequest);

            IncreaseCardBalanceRequest request = IncreaseCardBalanceRequest.newBuilder()
                    .setAmountToUpdate(price)
                    .setCardNumber(cardById.getCardNumber())
                    .setId(cardId)
                    .build();
            ticketRepository.deleteById(ticketId);
            cardGrpcClientService.increaseCardBalance(request);

            userRepository.findById(ticket.getUserId()).ifPresent(user -> {
                String subject = "Rezervasiyanın Ləğvi";
                String body = String.format("Hörmətli %s,\n\n%d nömrəli otaq üçün olan rezervasiyanız uğurla ləğv edildi. Ödənişiniz kartınıza geri qaytarıldı.",
                        user.getUsername(),
                        roomNumber);
                byRoomNumber.setReserved(false);
                mailService.sendMail(user.getEmail(), subject, body);
            });

            return "Your money is back and Ticket cancelled";
        } catch (Exception e) {
            throw new SomethingWentWrong("Something went wrong");
        }

    }






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
