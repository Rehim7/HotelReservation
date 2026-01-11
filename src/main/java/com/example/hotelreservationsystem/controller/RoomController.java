package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.dto.request.MailRequest;
import com.example.hotelreservationsystem.dto.request.RoomRequest;
import com.example.hotelreservationsystem.dto.request.TicketRequest;
import com.example.hotelreservationsystem.dto.request.UserOpininonRequest;
import com.example.hotelreservationsystem.dto.response.RoomResponse;
import com.example.hotelreservationsystem.exceptions.RoomReservedException;
import com.example.hotelreservationsystem.model.Room;
import com.example.hotelreservationsystem.repository.RoomRepository;
import com.example.hotelreservationsystem.service.RoomService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/hotelReservationSystem/room")

public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;

    }

    @PostMapping("/createRoom")
    @PreAuthorize("hasRole('ROLE_HOTELOWNER')")
    public RoomResponse createRoom(@RequestBody RoomRequest request) {
        return roomService.createRoom(request);
    }

    @DeleteMapping("/deleteRoom/{id}")
    @PreAuthorize("hasRole('ROLE_HOTELOWNER')")
    public void deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
    }

    @GetMapping("/findUnreservedRooms")
    public List<Room> findUnreservedRooms() {
        return roomService.findUnreservedRooms();
    }

    @GetMapping("/getAllRooms")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_HOTELOWNER')")
    public List<RoomResponse> getAllRooms() {
        return roomService.findAll();
    }


    @PostMapping("/userOpinionSetToRoom/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_HOTELOWNER')")
    public void userOpinionSetToRoom(@PathVariable Long id, @Valid @RequestBody UserOpininonRequest userOpininonRequest) {
        roomService.userOpinionSetToRoom(id,userOpininonRequest);
    }

    @GetMapping("/findByRoomNumber/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_HOTELOWNER')")
    public List<Room> findByRoomNumber(@PathVariable int id) {
        return  roomService.findRoomByRoomNumber(id);
    }


    @PostMapping("/reserveRoom/{cardId}/{roomNumber}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public void reserveRoom(@PathVariable Long cardId, @PathVariable int roomNumber,
                            @Valid @RequestBody TicketRequest ticketRequest, MailRequest mailRequest) throws Exception {
        roomService.reserveRoom(cardId,ticketRequest,roomNumber,mailRequest);
    }


    @PostMapping("/unreservRoom/{cardId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public void unReserveRoom(@PathVariable Long cardId) {
        roomService.unreserveRoom(cardId);
    }

    @PatchMapping("/rating/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_HOTELOWNER')")
    public ResponseEntity<Double> calculateRoomAverageRating(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.calculateRoomAverageRating(id));
    }


}



