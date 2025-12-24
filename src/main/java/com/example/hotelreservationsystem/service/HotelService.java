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

import java.util.List;

@Service
public class HotelService {
    private final HotelRepository hotelRepository;
    private final UserRepository  userRepository;
    private final UserOpininonsRepository  userOpininonsRepository;
    private final MailService mailService;

    private static final String Hotel_Data = "Hotel";
    public HotelService(HotelRepository hotelRepository, UserRepository userRepository, UserOpininonsRepository userOpininonsRepository, MailService mailService) {
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
        this.userOpininonsRepository = userOpininonsRepository;
        this.mailService = mailService;
    }


    @Transactional
    public HotelResponse createHotel(HotelRequest hotelRequest, MailRequest mailRequest) {
        Hotel hotel = new Hotel();
        hotel.setHotelName(hotelRequest.getHotelName());
        hotel.setHotelAddress(hotelRequest.getHotelAddress());
        hotel.setHotelImageUrl(hotelRequest.getHotelImageUrl());
        hotel.setHotelStars(0);
        hotel.setHotelDescription(hotelRequest.getHotelDescription());
        hotel.setHotelOwner(hotelRequest.getHotelOwner());
        hotel.setUserOpinions(null);

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


    @Cacheable(Hotel_Data)
    public List<HotelResponse> findAll() {
        return hotelRepository.findAll().stream()
                .map(hotel -> {
                    HotelResponse hotelResponse = new HotelResponse();
                    hotelResponse.setId(hotel.getId());
                    hotelResponse.setHotelName(hotel.getHotelName());
                    hotelResponse.setHotelAddress(hotel.getHotelAddress());
                    hotelResponse.setHotelImageUrl(hotel.getHotelImageUrl());
                    hotelResponse.setHotelStars(hotel.getHotelStars());
                    hotelResponse.setHotelDescription(hotel.getHotelDescription());
                    hotelResponse.setUserOpinions(hotel.getUserOpinions());
                    hotelResponse.setRooms(hotel.getRooms());
                    return hotelResponse;
                }).toList();
    }

    public Hotel findById(Long id) {
        return hotelRepository.findById(id).get();
    }
    public Hotel findByHotelName(String hotelName) {
        return hotelRepository.findByHotelName(hotelName);
    }

    @Transactional
    public void userOpinionSetToHotel(Long hotelId, UserOpininonRequest request) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + hotelId));
        User user = (User) userRepository.findUserByUsername(request.getUser());
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
