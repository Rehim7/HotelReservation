package com.example.hotelreservationsystem.service;

import com.example.hotelreservationsystem.dto.request.MailRequest;
import com.example.hotelreservationsystem.dto.request.RoomRequest;
import com.example.hotelreservationsystem.dto.request.TicketRequest;
import com.example.hotelreservationsystem.dto.request.UserOpininonRequest;
import com.example.hotelreservationsystem.dto.response.RoomResponse;
import com.example.hotelreservationsystem.exceptions.RoomNotFound;
import com.example.hotelreservationsystem.exceptions.RoomReservedException;
import com.example.hotelreservationsystem.model.*;
import com.example.hotelreservationsystem.repository.RoomRepository;
import com.example.hotelreservationsystem.repository.UserOpininonsRepository;
import com.example.hotelreservationsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {
    private final RoomRepository repository;
    private final UserRepository userRepository;
    private final TicketService ticketService;
    private final CardService cardService;
    private final UserOpininonsRepository  userOpininonsRepository;
    private final MailService mailService;

    private static final String Room_Data = "Room";


    public RoomService(RoomRepository repository, UserRepository userRepository, TicketService ticketService, CardService cardService, UserOpininonsRepository userOpininonsRepository, MailService mailService) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.ticketService = ticketService;
        this.cardService = cardService;
        this.userOpininonsRepository = userOpininonsRepository;

        this.mailService = mailService;
    }
    public void userOpinionSetToRoom(Long roomId, UserOpininonRequest request) {
        Room room = repository.findById(roomId).orElseThrow(() -> new RoomNotFound("Room not found"));
        User users = (User) userRepository.findUserByUsername(request.getUser());
        UserOpinions newOpinion = new UserOpinions();
        newOpinion.setUserOpinions(request.getUserOpinions());
        newOpinion.setUser(users);
        newOpinion.setRating(request.getRating());
        userOpininonsRepository.save(newOpinion);
        room.getUserOpinions().add(newOpinion);
        repository.save(room);
    }

    @Cacheable(value = Room_Data)
    public List<RoomResponse> findAll() {
        return repository.findAll().stream()
                .map(room -> {
                            RoomResponse roomResponse = new RoomResponse();
                            roomResponse.setId(room.getId());
                            roomResponse.setOwnerUser(room.getOwnerUser());
                            roomResponse.setRoomView(room.getRoomView());
                            roomResponse.setPrice(room.getPrice());
                            roomResponse.setRoomNumber(room.getRoomNumber());
                            roomResponse.setReserved(room.isReserved());
                            roomResponse.setUserOpinions(room.getUserOpinions());
                            roomResponse.setRoomStar(room.getRoomStar());
                            roomResponse.setDescription(room.getDescription());
                            return roomResponse;
                        }
                ).toList();
    }


    @Cacheable(value = Room_Data)
    public List<Room> findUnreservedRooms(){
        List<Room> byIsReserved = repository.findByIsReserved(false);
        return byIsReserved;
    }

    @Transactional
    public double calculateRoomAverageRating(Long roomId) {
        Room room = repository.getRoomByid(roomId).orElseThrow(() -> new RoomNotFound("Room not found"));
        List<UserOpinions> userOpinions = room.getUserOpinions();
        if (userOpinions == null || userOpinions.isEmpty()) {
            return 0.0;
        }
        double sum = 0;
        for (UserOpinions opinion : userOpinions) {
            sum += opinion.getRating();
        }
        double average = sum / userOpinions.size();
        room.setRoomStar(average);
        repository.save(room);
        return average;
    }



    public RoomResponse createRoom(RoomRequest roomRequest) {
        Room room = new Room();
        room.setRoomNumber(roomRequest.getRoomNumber());
        room.setRoomView(roomRequest.getRoomView());
        room.setDescription(roomRequest.getDescription());
        room.setPrice(roomRequest.getPrice());
        room.setRoomNumber(roomRequest.getRoomNumber());
        room.setBelongingHotel(roomRequest.getBelongingHotel());
        room.setReserved(false);
        room.setOwnerUser(null);
        room.setUserOpinions(null);

        repository.save(room);
        RoomResponse roomResponse = new RoomResponse();
        roomResponse.setRoomNumber(room.getRoomNumber());
        roomResponse.setRoomView(room.getRoomView());
        roomResponse.setDescription(room.getDescription());
        roomResponse.setPrice(room.getPrice());
        roomResponse.setRoomNumber(room.getRoomNumber());
        roomResponse.setBelongingHotel(room.getBelongingHotel());
        roomResponse.setReserved(false);
        roomResponse.setUserOpinions(null);
        roomResponse.setOwnerUser(null);
        roomResponse.setBelongingHotel(room.getBelongingHotel());
        return roomResponse;



    }

    @Transactional
    @CacheEvict(value = Room_Data,allEntries = true)
    public void deleteRoom(Long id) {
        repository.deleteById(id);
    }


    @Cacheable(value = Room_Data,key = "#roomNumber")
    public List<Room> findRoomByRoomNumber(int roomNumber) {
        List<Room> roomByRoomNumber = repository.getRoomByRoomNumber(roomNumber);
        return roomByRoomNumber;
    }

    public void reserveRoom(Long cardId, String userHolderName, TicketRequest ticketRequest, int roomNumber, MailRequest mailRequest) throws Exception {
        Room roomByRoomNumber = repository.findByRoomNumber(roomNumber);
        if (!roomByRoomNumber.isReserved()){
            ticketService.buyTicket(cardId,userHolderName,ticketRequest,roomNumber);
            try {
                mailService.sendMail(mailRequest.getTo(),mailRequest.getSubject(),mailRequest.getBody());
            } catch (Exception e) {
                throw new Exception("Mail couldnt send");
            }
        } else {
            throw new RoomReservedException("Room Reserved");
        }
    }

    @Transactional
    public void unreserveRoom(Long cardId, String userHolderName) {
        ticketService.cancelTicket(cardId,userHolderName);
    }


}
