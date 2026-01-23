package com.example.hotelreservationsystem.service;

import com.example.hotelreservationsystem.dto.request.RoomRequest;
import com.example.hotelreservationsystem.dto.request.TicketRequest;
import com.example.hotelreservationsystem.dto.request.UserOpininonRequest;
import com.example.hotelreservationsystem.dto.response.RoomResponse;
import com.example.hotelreservationsystem.exceptions.*;
import com.example.hotelreservationsystem.model.*;
import com.example.hotelreservationsystem.repository.HotelRepository;
import com.example.hotelreservationsystem.repository.RoomRepository;
import com.example.hotelreservationsystem.repository.UserOpininonsRepository;
import com.example.hotelreservationsystem.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RoomService {
    private final RoomRepository repository;
    private final UserRepository userRepository;
    private final TicketService ticketService;
    private final HotelRepository hotelRepository;
    // private final CardService cardService;
    private final UserOpininonsRepository userOpininonsRepository;
    private final MailService mailService;
    private final R2StorageService r2StorageService;

    private static final String Room_Data = "Room";

    public RoomService(RoomRepository repository, UserRepository userRepository, TicketService ticketService,
            HotelRepository hotelRepository, UserOpininonsRepository userOpininonsRepository, MailService mailService,
            R2StorageService r2StorageService) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.ticketService = ticketService;
        this.hotelRepository = hotelRepository;
        this.userOpininonsRepository = userOpininonsRepository;
        this.r2StorageService = r2StorageService;

        this.mailService = mailService;
    }

    @Transactional
    public void userOpinionSetToRoom(Long roomId, UserOpininonRequest request) {
        Room room = repository.findById(roomId).orElseThrow(() -> new RoomNotFound("Room not found"));
        User users = (User) userRepository.findUserById(request.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.getUserId()));
        UserOpinions newOpinion = new UserOpinions();
        newOpinion.setUserOpinions(request.getUserOpinions());
        newOpinion.setUser(users);
        newOpinion.setRating(request.getRating());
        userOpininonsRepository.save(newOpinion);
        room.getUserOpinions().add(newOpinion);
        repository.save(room);
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

    @Transactional
    public RoomResponse createRoom(RoomRequest roomRequest) {

        if (!repository.findByRoomNumber(roomRequest.getRoomNumber()).isEmpty()){
            throw new RoomAlreadyExist("Room number already exist");
        }

        Room room = new Room();
        room.setRoomNumber(roomRequest.getRoomNumber());
        String r2ImageUrl;
        try {
            if (roomRequest.getRoomView() != null && !roomRequest.getRoomView().trim().isEmpty()) {
                r2ImageUrl = r2StorageService.uploadImageFromUrl(roomRequest.getRoomView());
                room.setRoomView(r2ImageUrl);
            } else {
                room.setRoomView(null);
            }
        } catch (IOException e) {
            throw new RuntimeException("Şəkil URL-dən yüklənib R2-yə upload edilərkən xəta: " + e.getMessage(), e);
        }

        Hotel hotel = hotelRepository.findById(roomRequest.getBelongingHotelId())
                .orElseThrow(() -> new HotelNotFoundException(
                        "Hotel not found with id: " + roomRequest.getBelongingHotelId()));

        room.setDescription(roomRequest.getDescription());
        room.setPrice(roomRequest.getPrice());
        room.setReserved(false);
        room.setOwnerUser(null);
        room.setRoomType(roomRequest.getRoomType());
        room.setUserOpinions(new ArrayList<>());
        hotel.addRoom(room);

        hotelRepository.save(hotel);

        RoomResponse roomResponse = new RoomResponse();
        roomResponse.setId(room.getId());
        roomResponse.setRoomType(room.getRoomType());
        roomResponse.setRoomNumber(room.getRoomNumber());
        roomResponse.setRoomView(room.getRoomView());
        roomResponse.setDescription(room.getDescription());
        roomResponse.setPrice(room.getPrice());
        roomResponse.setReserved(false);
        roomResponse.setRoomStar(room.getRoomStar() != null ? room.getRoomStar() : 0.0);
        roomResponse.setUserOpinions(new ArrayList<>());
        roomResponse.setOwnerUserEmail(null);
        roomResponse.setBelongingHotelId(room.getBelongingHotel() != null ? room.getBelongingHotel().getId() : null);
        return roomResponse;
    }

    @Transactional
    @CacheEvict(value = Room_Data, allEntries = true)
    public void deleteRoom(Long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = Room_Data, key = "#roomNumber")
    public List<RoomResponse> findRoomByRoomNumber(int roomNumber) {
        List<Room> rooms = repository.findByRoomNumber(roomNumber);
        return rooms.stream()
                .map(room -> {
                    RoomResponse roomResponse = new RoomResponse();
                    roomResponse.setId(room.getId());
                    roomResponse.setRoomView(room.getRoomView());
                    roomResponse.setPrice(room.getPrice());
                    roomResponse.setRoomNumber(room.getRoomNumber());
                    roomResponse.setRoomType(room.getRoomType());
                    roomResponse.setReserved(room.isReserved());
                    roomResponse.setRoomStar(room.getRoomStar() != null ? room.getRoomStar() : 0.0);
                    roomResponse.setDescription(room.getDescription());
                    roomResponse.setBelongingHotelId(
                            room.getBelongingHotel() != null ? room.getBelongingHotel().getId() : null);
                    roomResponse.setOwnerUserEmail(room.getOwnerUser() != null ? room.getOwnerUser().getEmail() : null);

                    List<String> opinionTexts = new ArrayList<>();
                    if (room.getUserOpinions() != null) {
                        room.getUserOpinions().forEach(op -> opinionTexts.add(op.getUserOpinions()));
                    }
                    roomResponse.setUserOpinions(opinionTexts);

                    return roomResponse;
                })
                .toList();
    }

    @Transactional
    @CacheEvict(value = Room_Data, key = "#roomId")
    public String uploadImageToRoom(Long roomId, MultipartFile file) throws IOException {
        Room room = repository.findById(roomId)
                .orElseThrow(() -> new RoomNotFound("Room not found with id: " + roomId));
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (room.getBelongingHotel() == null || room.getBelongingHotel().getHotelOwner() == null
                || !Objects.equals(room.getBelongingHotel().getHotelOwner().getEmail(), currentUserEmail)) {
            throw new AccessDeniedException("You do not have permission to upload an image for this room.");
        }
        String imageUrl = r2StorageService.uploadFile(file);
        room.setRoomView(imageUrl);
        repository.save(room);

        return imageUrl;
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> findAllRoomsByHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + hotelId));
        return hotel.getRooms().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> findAvailableRoomsByHotelId(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + hotelId));
        return hotel.getRooms().stream()
                .filter(room -> !room.isReserved())
                .map(this::mapToResponse)
                .toList();
    }

    private RoomResponse mapToResponse(Room room) {
        RoomResponse roomResponse = new RoomResponse();
        roomResponse.setId(room.getId());
        roomResponse.setRoomView(room.getRoomView());
        roomResponse.setPrice(room.getPrice());
        roomResponse.setRoomNumber(room.getRoomNumber());
        roomResponse.setRoomType(room.getRoomType());
        roomResponse.setReserved(room.isReserved());
        roomResponse.setRoomStar(room.getRoomStar() != null ? room.getRoomStar() : 0.0);
        roomResponse.setDescription(room.getDescription());
        roomResponse.setBelongingHotelId(room.getBelongingHotel() != null ? room.getBelongingHotel().getId() : null);
        roomResponse.setOwnerUserEmail(room.getOwnerUser() != null ? room.getOwnerUser().getEmail() : null);

        List<String> opinionTexts = new ArrayList<>();
        if (room.getUserOpinions() != null) {
            room.getUserOpinions().forEach(op -> opinionTexts.add(op.getUserOpinions()));
        }
        roomResponse.setUserOpinions(opinionTexts);

        return roomResponse;
    }



}

// =========================================================================================================

// @Transactional
// public void reserveRoom(Long cardId, TicketRequest ticketRequest, Long
// roomNumber, MailRequest mailRequest) throws Exception {
// Room roomByRoomNumber = repository.findByRoomNumber(roomNumber);
// if (!roomByRoomNumber.isReserved()){
// ticketService.buyTicket(cardId,ticketRequest,roomNumber);
// try {
// mailService.sendMail(mailRequest.getTo(),mailRequest.getSubject(),mailRequest.getBody());
// } catch (Exception e) {
// throw new Exception("Mail couldnt send");
// }
// } else {
// throw new RoomReservedException("Room Reserved");
// }
// }
//
// @Transactional
// public void unreserveRoom(Long cardId) {
// ticketService.cancelTicket(cardId);
// }
//
// @Transactional(readOnly = true)
// @Cacheable(value = Room_Data)
// public List<RoomResponse> findAll() {
// return repository.findAll().stream()
// .map(room -> {
// RoomResponse roomResponse = new RoomResponse();
// roomResponse.setId(room.getId());
// roomResponse.setRoomView(room.getRoomView());
// roomResponse.setPrice(room.getPrice());
// roomResponse.setRoomNumber(room.getRoomNumber());
// roomResponse.setReserved(room.isReserved());
// roomResponse.setRoomStar(room.getRoomStar() != null ? room.getRoomStar() :
// 0.0);
// roomResponse.setDescription(room.getDescription());
//
// // Lazy association-ları serialize etməmək üçün sadə dəyərlər ötürürük
// roomResponse.setBelongingHotelId(room.getBelongingHotel() != null ?
// room.getBelongingHotel().getId() : null);
// roomResponse.setOwnerUserEmail(room.getOwnerUser() != null ?
// room.getOwnerUser().getEmail() : null);
//
// // UserOpinions daxilində user proxy-ləri var, ona görə sadəcə mətnləri
// göndəririk
// List<String> opinionTexts = new ArrayList<>();
// if (room.getUserOpinions() != null) {
// room.getUserOpinions().forEach(op -> opinionTexts.add(op.getUserOpinions()));
// }
// roomResponse.setUserOpinionTexts(opinionTexts);
//
// return roomResponse;
// })
// .toList();
// }
// @Transactional(readOnly = true)
// @Cacheable(value = Room_Data)
// public List<RoomResponse> findUnreservedRooms(){
// List<Room> rooms = repository.findByIsReserved(false);
// return rooms.stream()
// .map(room -> {
// RoomResponse roomResponse = new RoomResponse();
// roomResponse.setId(room.getId());
// roomResponse.setRoomView(room.getRoomView());
// roomResponse.setPrice(room.getPrice());
// roomResponse.setRoomNumber(room.getRoomNumber());
// roomResponse.setReserved(room.isReserved());
// roomResponse.setRoomStar(room.getRoomStar() != null ? room.getRoomStar() :
// 0.0);
// roomResponse.setDescription(room.getDescription());
// roomResponse.setBelongingHotelId(room.getBelongingHotel() != null ?
// room.getBelongingHotel().getId() : null);
// roomResponse.setOwnerUserEmail(room.getOwnerUser() != null ?
// room.getOwnerUser().getEmail() : null);
//
// List<String> opinionTexts = new ArrayList<>();
// if (room.getUserOpinions() != null) {
// room.getUserOpinions().forEach(op -> opinionTexts.add(op.getUserOpinions()));
// }
// roomResponse.setUserOpinionTexts(opinionTexts);
//
// return roomResponse;
// })
// .toList();
// }
