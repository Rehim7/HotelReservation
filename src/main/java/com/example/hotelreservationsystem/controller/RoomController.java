package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.dto.request.RoomRequest;
import com.example.hotelreservationsystem.dto.request.UserOpininonRequest;
import com.example.hotelreservationsystem.dto.response.RoomResponse;
import com.example.hotelreservationsystem.service.RoomService;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.createRoom(request));
    }

    @DeleteMapping("/deleteRoom/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/findUnreservedRooms/{hotelId}")
    public ResponseEntity<List<RoomResponse>> findUnreservedRooms(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.findAvailableRoomsByHotelId(hotelId));
    }

    @GetMapping("/getAllRooms/{hotelId}")
    public ResponseEntity<List<RoomResponse>> getAllRooms(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.findAllRoomsByHotel(hotelId));
    }

    @PostMapping("/userOpinionSetToRoom/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> userOpinionSetToRoom(
            @PathVariable Long id,
            @Valid @RequestBody UserOpininonRequest userOpininonRequest,
            @RequestHeader("Authorization") String authHeader) {
        roomService.userOpinionSetToRoom(id, userOpininonRequest, authHeader);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/findByRoomNumber/{roomNumber}")
    public ResponseEntity<List<RoomResponse>> findByRoomNumber(@PathVariable int roomNumber) {
        return ResponseEntity.ok(roomService.findRoomByRoomNumber(roomNumber));
    }

    @PatchMapping("/rating/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Double> calculateRoomAverageRating(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.calculateRoomAverageRating(id));
    }
}