package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.dto.request.ReserveRoomCombinedRequest;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/hotelReservationSystem/room")

public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;

    }

    @PostMapping("/createRoom")
    @PreAuthorize("isAuthenticated()")
    public RoomResponse createRoom(@RequestBody RoomRequest request) {
        return roomService.createRoom(request);
    }

    @DeleteMapping("/deleteRoom/{id}")
    @PreAuthorize("isAuthenticated()")
    public void deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
    }

//    @GetMapping("/findUnreservedRooms")
//    public List<RoomResponse> findUnreservedRooms() {
//        return roomService.findUnreservedRooms();
//    }

//    @GetMapping("/getAllRooms")
//    @PreAuthorize("isAuthenticated()")
//    public List<RoomResponse> getAllRooms() {
//        return roomService.findAll();
//    }

    @PostMapping("/userOpinionSetToRoom/{id}")
    @PreAuthorize("isAuthenticated()")
    public void userOpinionSetToRoom(@PathVariable Long id,
            @Valid @RequestBody UserOpininonRequest userOpininonRequest) {
        roomService.userOpinionSetToRoom(id, userOpininonRequest);
    }

    @GetMapping("/findByRoomNumber/{id}")
    @PreAuthorize("isAuthenticated()")
    public List<RoomResponse> findByRoomNumber(@PathVariable int id) {
        return roomService.findRoomByRoomNumber(id);
    }

//    @PostMapping("/reserveRoom/{cardId}/{roomNumber}")
//    @PreAuthorize("isAuthenticated()")
//    public void reserveRoom(@PathVariable Long cardId, @PathVariable Long roomNumber,
//            @Valid @RequestBody ReserveRoomCombinedRequest request) throws Exception {
//        roomService.reserveRoom(cardId, request.getTicketRequest(), roomNumber, request.getMailRequest());
//    }
//
//    @PostMapping("/unreservRoom/{cardId}")
//    @PreAuthorize("isAuthenticated()")
//    public void unReserveRoom(@PathVariable Long cardId) {
//        roomService.unreserveRoom(cardId);
//    }

    @PatchMapping("/rating/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Double> calculateRoomAverageRating(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.calculateRoomAverageRating(id));
    }

    @PostMapping(value = "/uploadImage/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> uploadRoomImage(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file)
            throws IOException {
        String imageUrl = roomService.uploadImageToRoom(id, file);
        return ResponseEntity.ok(imageUrl);
    }

}
