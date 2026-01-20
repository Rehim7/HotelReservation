package com.example.hotelreservationsystem.service;

import com.example.hotelreservationsystem.dto.request.HotelRequest;
import com.example.hotelreservationsystem.dto.request.MailRequest;
import com.example.hotelreservationsystem.dto.request.UserOpininonRequest;
import com.example.hotelreservationsystem.dto.response.HotelResponse;
import com.example.hotelreservationsystem.exceptions.HotelNotFoundException;
import com.example.hotelreservationsystem.model.Hotel;
import com.example.hotelreservationsystem.model.Room;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.model.UserOpinions;
import com.example.hotelreservationsystem.repository.HotelRepository;
import com.example.hotelreservationsystem.repository.UserOpininonsRepository;
import com.example.hotelreservationsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HotelService {
    private final HotelRepository hotelRepository;
    private final UserRepository  userRepository;
    private final UserOpininonsRepository  userOpininonsRepository;
    private final MailService mailService;
    private final R2StorageService r2StorageService;

    private static final String Hotel_Data = "Hotel";
    public HotelService(HotelRepository hotelRepository, UserRepository userRepository, UserOpininonsRepository userOpininonsRepository, MailService mailService, R2StorageService r2StorageService) {
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
        this.userOpininonsRepository = userOpininonsRepository;
        this.mailService = mailService;
        this.r2StorageService = r2StorageService;
    }


    @Transactional
    public HotelResponse createHotel(HotelRequest hotelRequest, MailRequest mailRequest) {
        Hotel hotel = new Hotel();
        hotel.setHotelName(hotelRequest.getHotelName());
        hotel.setHotelAddress(hotelRequest.getHotelAddress());

        String r2ImageUrl;
        try {
            r2ImageUrl = r2StorageService.uploadImageFromUrl(hotelRequest.getHotelImageUrl());
            hotel.setHotelImageUrl(r2ImageUrl);
        } catch (IOException e) {
            throw new RuntimeException("Şəkil URL-dən yüklənib R2-yə upload edilərkən xəta: " + e.getMessage(), e);
        }
        
        hotel.setHotelStars(0);
        hotel.setHotelDescription(hotelRequest.getHotelDescription());
        hotel.setHotelOwner(userRepository.findByEmail(hotelRequest.getHotelOwner())
                                         .orElseThrow(() -> new UsernameNotFoundException("Hotel owner not found with email: " + hotelRequest.getHotelOwner())));
        hotel.setUserOpinions(new ArrayList<>());

        hotelRepository.save(hotel);
        HotelResponse hotelResponse = new HotelResponse();
        hotelResponse.setId(hotel.getId());
        hotelResponse.setHotelName(hotel.getHotelName());
        hotelResponse.setHotelAddress(hotel.getHotelAddress());
        hotelResponse.setHotelImageUrl(hotel.getHotelImageUrl());
        hotelResponse.setHotelStars(hotel.getHotelStars());
        hotelResponse.setHotelDescription(hotel.getHotelDescription());
        hotelResponse.setUserOpinions(hotel.getUserOpinions());
        hotelResponse.setRooms(hotel.getRooms());
        mailService.sendMail(mailRequest.getTo(),mailRequest.getSubject(),mailRequest.getBody());
        return hotelResponse;
    }

    @Transactional
    @CacheEvict(value = Hotel_Data,allEntries = true)
    public void  deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }


    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @Cacheable(Hotel_Data)
    public List<HotelResponse> findAll() {
        List<Hotel> hotels = hotelRepository.findAll();
        List<HotelResponse> responses = new ArrayList<>();
        
        for (Hotel hotel : hotels) {
            HotelResponse hotelResponse = new HotelResponse();
            hotelResponse.setId(hotel.getId());
            hotelResponse.setHotelName(hotel.getHotelName());
            hotelResponse.setHotelAddress(hotel.getHotelAddress());
            hotelResponse.setHotelImageUrl(hotel.getHotelImageUrl());
            hotelResponse.setHotelStars(hotel.getHotelStars());
            hotelResponse.setHotelDescription(hotel.getHotelDescription());

            if (hotel.getUserOpinions() != null) {
                hotelResponse.setUserOpinions(new ArrayList<>(hotel.getUserOpinions()));
            } else {
                hotelResponse.setUserOpinions(new ArrayList<>());
            }
            
            if (hotel.getRooms() != null) {
                hotelResponse.setRooms(new ArrayList<>(hotel.getRooms()));
            } else {
                hotelResponse.setRooms(new ArrayList<>());
            }
            
            responses.add(hotelResponse);
        }

        return responses;
    }

    public HotelResponse findById(Long id) {
        Optional<Hotel> byId = hotelRepository.findById(id);
        HotelResponse hotelResponse = new HotelResponse();
        hotelResponse.setId(id);
        hotelResponse.setHotelName(byId.get().getHotelName());
        hotelResponse.setHotelAddress(byId.get().getHotelAddress());
        hotelResponse.setHotelImageUrl(byId.get().getHotelImageUrl());
        hotelResponse.setHotelStars(byId.get().getHotelStars());
        hotelResponse.setHotelDescription(byId.get().getHotelDescription());
        hotelResponse.setRooms(byId.get().getRooms());
        hotelResponse.setUserOpinions(byId.get().getUserOpinions());
        return hotelResponse;
    }
    public Hotel findByHotelName(String hotelName) {
        return hotelRepository.findByHotelName(hotelName);
    }

    @Transactional
    public void userOpinionSetToHotel(Long hotelId, UserOpininonRequest request) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + hotelId));
        User user = userRepository.findByEmail(request.getUser())
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getUser()));
        UserOpinions newOpinion = new UserOpinions();
        newOpinion.setUserOpinions(request.getUserOpinions());
        newOpinion.setUser(user);
        newOpinion.setRating(request.getRating());

        userOpininonsRepository.save(newOpinion);

        hotel.getUserOpinions().add(newOpinion);
        hotelRepository.save(hotel);
    }


    @Transactional
    public double calculateHotelAverageRating(Long hotelId) {
        Hotel hotel = hotelRepository.getHotelById(hotelId);
        List<Room> rooms = hotel.getRooms();
        double total = 0;

        if (rooms.isEmpty()) {
            return 0.0;
        }
        for (Room room : rooms) {
            total += room.getRoomStar();
        }

        double average = total / rooms.size();
        hotel.setHotelStars(average);
        hotelRepository.save(hotel);
        return average;
    }




}
