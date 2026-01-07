package com.example.hotelreservationsystem.controller;

import com.example.hotelreservationsystem.dto.request.HotelRequest;
import com.example.hotelreservationsystem.dto.request.MailRequest;
import com.example.hotelreservationsystem.dto.request.UserOpininonRequest;
import com.example.hotelreservationsystem.dto.response.HotelResponse;
import com.example.hotelreservationsystem.model.Hotel;
import com.example.hotelreservationsystem.service.HotelService;
import com.example.hotelreservationsystem.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/hotelReservationSystem/hotel")

public class HotelController {
    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {this.hotelService = hotelService;
    }

    @PostMapping("/createHotel")
    @PreAuthorize("hasRole('ROLE_HOTELOWNER')")
    public HotelResponse createHotel(@Valid @RequestBody HotelRequest hotelRequest) {
        return hotelService.createHotel(hotelRequest, hotelRequest.getMailRequest());
    }

    @DeleteMapping("/deleteHotel/{hotelId}")
    @PreAuthorize("hasRole('ROLE_HOTELOWNER')")
    public void deleteHotel(@PathVariable("hotelId") Long hotelId) {
        hotelService.deleteHotel(hotelId);
    }

    @GetMapping("/getAllHotels")
    public List<HotelResponse> getAllHotels() {
        return hotelService.findAll();
    }

    @GetMapping("/getHotelById/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_HOTELOWNER')")
    public Hotel getHotelById(@PathVariable("id") Long id) {
        return hotelService.findById(id);
    }

    @GetMapping("/getHotelByName/{hotelName}")
    public Hotel getHotelByName(@PathVariable("hotelName") String hotelName) {
        return hotelService.findByHotelName(hotelName);
    }

    @PostMapping("/userOpinionSetToHotel/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_HOTELOWNER')")
    public void userOpinionSetToHotel(@PathVariable Long id, @Valid @RequestBody UserOpininonRequest userOpininonRequest) {
        hotelService.userOpinionSetToHotel(id,userOpininonRequest);
    }
    @PatchMapping("/rating/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_HOTELOWNER')")
    public ResponseEntity<Double> calculateHotelAverageRating(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.calculateHotelAverageRating(id));
    }



}

