package com.example.hotelreservationsystem.service;

import com.example.hotelreservationsystem.dto.request.HotelRequest;
import com.example.hotelreservationsystem.dto.request.MailRequest;
import com.example.hotelreservationsystem.dto.request.UserOpininonRequest;
import com.example.hotelreservationsystem.dto.response.HotelResponse;
import com.example.hotelreservationsystem.dto.response.RoomResponse;
import com.example.hotelreservationsystem.exceptions.HotelNotFoundException;
import com.example.hotelreservationsystem.exceptions.RoomAlreadyExist;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class HotelService {
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final UserOpininonsRepository userOpininonsRepository;
    private final MailService mailService;
    private final R2StorageService r2StorageService;
    private final JwtService jwtService;
    private static final String Hotel_Data = "Hotel";

    public HotelService(HotelRepository hotelRepository, UserRepository userRepository,
                        UserOpininonsRepository userOpininonsRepository, MailService mailService,
                        R2StorageService r2StorageService, JwtService jwtService) {
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
        this.userOpininonsRepository = userOpininonsRepository;
        this.mailService = mailService;
        this.r2StorageService = r2StorageService;
        this.jwtService = jwtService;
    }

    @Transactional
    @CacheEvict(value = Hotel_Data, allEntries = true)
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
        hotel.setHotelStars(0.0);
        hotel.setHotelDescription(hotelRequest.getHotelDescription());
        hotel.setUserOpinions(new ArrayList<>());
        hotel.setRooms(new ArrayList<>());
        hotelRepository.save(hotel);
        try {
            mailService.sendMail(mailRequest.getTo(), mailRequest.getSubject(), mailRequest.getBody());
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
        return mapToResponse(hotel);
    }

    @Transactional
    @CacheEvict(value = Hotel_Data, allEntries = true)
    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @Cacheable(Hotel_Data)
    public List<HotelResponse> findAll() {
        List<Hotel> hotels = hotelRepository.findAll();
        List<HotelResponse> responses = new ArrayList<>();
        for (Hotel hotel : hotels) {
            responses.add(mapToResponse(hotel));
        }
        return responses;
    }

    public HotelResponse findById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + id));
        return mapToResponse(hotel);
    }

    private HotelResponse mapToResponse(Hotel hotel) {
        HotelResponse hotelResponse = new HotelResponse();
        hotelResponse.setId(hotel.getId());
        hotelResponse.setHotelName(hotel.getHotelName());
        hotelResponse.setHotelAddress(hotel.getHotelAddress());
        hotelResponse.setHotelImageUrl(hotel.getHotelImageUrl());
        hotelResponse.setHotelStars(hotel.getHotelStars());
        hotelResponse.setHotelDescription(hotel.getHotelDescription());

        List<String> opinions = new ArrayList<>();
        if (hotel.getUserOpinions() != null) {
            hotel.getUserOpinions().forEach(op -> opinions.add(op.getUserOpinions()));
        }
        hotelResponse.setUserOpinions(opinions);

        List<RoomResponse> roomResponses = new ArrayList<>();
        if (hotel.getRooms() != null) {
            hotel.getRooms().forEach(room -> {
                RoomResponse rr = new RoomResponse();
                rr.setId(room.getId());
                rr.setRoomNumber(room.getRoomNumber());
                rr.setPrice(room.getPrice());
                rr.setDescription(room.getDescription());
                rr.setRoomView(room.getRoomView());
                rr.setRoomStar(room.getRoomStar() != null ? room.getRoomStar() : 0.0);
                rr.setReserved(room.isReserved());
                rr.setRoomType(room.getRoomType());
                rr.setBelongingHotel(hotel.getHotelName ());
                List<String> opinionTexts = new ArrayList<>();
                if (room.getUserOpinions() != null) {
                    for (UserOpinions op : room.getUserOpinions()) {
                        opinionTexts.add(op.getUserOpinions());
                    }
                }
                rr.setUserOpinions(opinionTexts);
                roomResponses.add(rr);
            });
        }
        hotelResponse.setRooms(roomResponses);

        return hotelResponse;
    }

    public Hotel findByHotelName(String hotelName) {
        return hotelRepository.findByHotelName(hotelName);
    }

    @Transactional
    public void userOpinionSetToHotel(Long hotelId, UserOpininonRequest request,String authHeader) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + hotelId));
        String token = authHeader.substring(7);
        Long l = jwtService.extractUserId(token);
        UserOpinions newOpinion = new UserOpinions();
        newOpinion.setUserOpinions(request.getUserOpinions());
        newOpinion.setUserId(l);
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
