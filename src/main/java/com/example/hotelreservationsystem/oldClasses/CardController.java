//package com.example.hotelreservationsystem.controller;
//
//import com.example.hotelreservationsystem.dto.request.CardRequest;
//import com.example.hotelreservationsystem.dto.response.CardResponse;
//import com.example.hotelreservationsystem.model.Card;
//import com.example.hotelreservationsystem.service.CardService;
//import jakarta.validation.Valid;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("api/hotelReservationSystem/card")
//
//
//public class CardController {
//    private final CardService cardService;
//
//    public CardController(CardService cardService) {
//        this.cardService = cardService;
//    }
//
//    @PostMapping("/createCard")
//    @PreAuthorize("isAuthenticated()")
//    public CardResponse createCard(@Valid @RequestBody CardRequest cardRequest) {
//        return cardService.createCard(cardRequest);
//    }
//
//    @DeleteMapping("/deleteCard/{cardId}")
//    @PreAuthorize("isAuthenticated()")
//    public void deleteCard(@PathVariable("cardId") Long cardId) {
//        cardService.deleteCard(cardId);
//    }
//
//    @GetMapping("/getCardById/{cardId}")
//    @PreAuthorize("isAuthenticated()")
//    public Card getCardById(@PathVariable("cardId") Long cardId) {
//        return cardService.getCardById(cardId);
//    }
//
//    @PostMapping("/increaseCardBalance/{cardId}/{amountToUpdate}")
//    @PreAuthorize("isAuthenticated()")
//    public CardResponse increaseCardBalance(@PathVariable Long cardId, @PathVariable Long amountToUpdate) {
//        return cardService.increaseCardBalance(cardId, amountToUpdate);
//    }
//
//
//
//}
