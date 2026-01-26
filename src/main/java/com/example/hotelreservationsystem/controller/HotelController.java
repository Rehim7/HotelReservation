package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.dto.request.HotelRequest;
import com.example.hotelreservationsystem.dto.request.UserOpininonRequest;
import com.example.hotelreservationsystem.dto.response.HotelResponse;
import com.example.hotelreservationsystem.model.Hotel;
import com.example.hotelreservationsystem.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/hotelReservationSystem/hotel")
public class HotelController {
    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping("/createHotel")
    @PreAuthorize("hasRole('ADMIN')") // Hoteli hər kəs yox, admin yarada bilsin
    public ResponseEntity<HotelResponse> createHotel(@Valid @RequestBody HotelRequest hotelRequest) {
        return ResponseEntity.ok(hotelService.createHotel(hotelRequest, hotelRequest.getMailRequest()));
    }

    @DeleteMapping("/deleteHotel/{hotelId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHotel(@PathVariable("hotelId") Long hotelId) {
        hotelService.deleteHotel(hotelId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getAllHotels")
    public ResponseEntity<List<HotelResponse>> getAllHotels() {
        return ResponseEntity.ok(hotelService.findAll());
    }

    @GetMapping("/getHotelById/{id}")
    public ResponseEntity<HotelResponse> getHotelById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(hotelService.findById(id));
    }

    @GetMapping("/getHotelByName/{hotelName}")
    public ResponseEntity<Hotel> getHotelByName(@PathVariable("hotelName") String hotelName) {
        return ResponseEntity.ok(hotelService.findByHotelName(hotelName));
    }

    @PostMapping("/userOpinionSetToHotel/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> userOpinionSetToHotel(
            @PathVariable Long id,
            @Valid @RequestBody UserOpininonRequest userOpininonRequest,
            @RequestHeader("Authorization") String authHeader) {
        hotelService.userOpinionSetToHotel(id, userOpininonRequest, authHeader);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/rating/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Double> calculateHotelAverageRating(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.calculateHotelAverageRating(id));
    }
}