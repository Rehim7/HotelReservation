package com.example.hotelreservationsystem.service;

import com.example.hotelreservationsystem.dto.request.CardRequest;
import com.example.hotelreservationsystem.dto.response.CardResponse;
import com.example.hotelreservationsystem.exceptions.CardAlreadyExist;
import com.example.hotelreservationsystem.exceptions.CardNotFound;
import com.example.hotelreservationsystem.model.Card;
import com.example.hotelreservationsystem.model.User;
import com.example.hotelreservationsystem.repository.CardRepository;
import com.example.hotelreservationsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private static final String Card_Data = "Card";
    public CardService(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Cacheable(Card_Data)
    public CardResponse createCard(CardRequest cardRequest) {
        List<Card> cardByCardNumber = cardRepository.getCardByCardNumber(cardRequest.getCardNumber());
        if (!cardByCardNumber.isEmpty()) {
            throw new CardAlreadyExist("Card already exist");
        }
        User userByUserName = userRepository.findUserByUsername((cardRequest.getCardHolderName()));
        Card card = new Card();
        card.setCardNumber(cardRequest.getCardNumber());
        card.setCardBalance(0L);
        card.setCardHolderName(cardRequest.getCardHolderName());
        card.setExpirationDate(cardRequest.getExpirationDate());
        card.setCvv(cardRequest.getCvv());
        card.setUser(userByUserName);

        cardRepository.save(card);
        CardResponse cardResponse = new CardResponse();
        cardResponse.setCardBalance(card.getCardBalance());
        cardResponse.setId(card.getId());
        cardResponse.setCardNumber(card.getCardNumber());
        cardResponse.setCardHolderName(card.getCardHolderName());
        cardResponse.setExpirationDate(card.getExpirationDate());
        cardResponse.setCvv(card.getCvv());
        return cardResponse;
    }

    @Transactional
    @CacheEvict(value = Card_Data)
    public void deleteCard(Long id) {
        cardRepository.deleteById(id);
    }

    public Card getCardById(Long id) {
        return cardRepository.findById(id).orElseThrow();
    }

    @Transactional
    public CardResponse increaseCardBalance(Long id,Long amountToUpdate) {
        Card card = cardRepository.findById(id).orElseThrow();

        boolean equals = card.getCardHolderName().equals(SecurityContextHolder.getContext().getAuthentication().getName());
        if (equals) {
            card.setCardBalance(card.getCardBalance() + amountToUpdate);
            cardRepository.save(card);
        } else {
            throw new CardNotFound("Card not found");
        }
        CardResponse cardResponse = new CardResponse();
        cardResponse.setCardBalance(card.getCardBalance());
        cardResponse.setId(card.getId());
        cardResponse.setCardHolderName(card.getCardHolderName());
        cardResponse.setExpirationDate(card.getExpirationDate());
        cardResponse.setCvv(card.getCvv());
        return cardResponse;
    }

    @Transactional
    public CardResponse decreaseCardBalance(Long id, Long amountToUpdate) {
        Card card = cardRepository.findById(id).orElseThrow();
        boolean equals = card.getCardHolderName().equals(SecurityContextHolder.getContext().getAuthentication().getName());
        if (equals) {
            card.setCardBalance((long) (card.getCardBalance() - amountToUpdate));
            cardRepository.save(card);
        } else {
            throw new CardNotFound("Card not found");
        }
        CardResponse cardResponse = new CardResponse();
        cardResponse.setCardBalance(card.getCardBalance());
        cardResponse.setId(card.getId());
        cardResponse.setCardHolderName(card.getCardHolderName());
        cardResponse.setExpirationDate(card.getExpirationDate());
        cardResponse.setCvv(card.getCvv());
        cardResponse.setCardNumber(card.getCardNumber());
        return cardResponse;
    }




}
