package com.example.hotelreservationsystem.service;

import com.example.banksystem.proto.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CardGrpcClientService {

    private static final Logger log = LoggerFactory.getLogger(CardGrpcClientService.class);

    @GrpcClient("cardService")
    private CardServiceGrpc.CardServiceBlockingStub cardServiceBlockingStub;

    @CircuitBreaker(name = "hotelReservationSystem", fallbackMethod = "handleCardResponseFallback")
    public CardResponse getCardByUserId(GetCardByUserIdRequest request) {
        return cardServiceBlockingStub.getCardByUserId(request);
    }

    @CircuitBreaker(name = "hotelReservationSystem", fallbackMethod = "handleCardResponseFallback")
    public CardResponse createCard(CreateCardRequest request) {
        return cardServiceBlockingStub.createCard(request);
    }

    @CircuitBreaker(name = "hotelReservationSystem", fallbackMethod = "handleCardResponseFallback")
    public CardResponse getCardById(GetCardByIdRequest request) {
        return cardServiceBlockingStub.getCardById(request);
    }

    @CircuitBreaker(name = "hotelReservationSystem", fallbackMethod = "handleCardResponseFallback")
    public CardResponse increaseCardBalance(IncreaseCardBalanceRequest request) {
        return cardServiceBlockingStub.increaseCardBalance(request);
    }

    @CircuitBreaker(name = "hotelReservationSystem", fallbackMethod = "handleCardResponseFallback")
    public CardResponse decreaseCardBalance(DecreaseCardBalanceRequest request) {
        return cardServiceBlockingStub.decreaseCardBalance(request);
    }
    public CardResponse handleCardResponseFallback(Exception e) {
        log.error("Bank servisi əlçatan deyil. Circuit Breaker statusu yoxlanılır. Səbəb: {}", e.getMessage());
        throw new RuntimeException("Ödəniş xidməti hal-hazırda işləmir. Zəhmət olmasa bir az sonra yoxlayın.");
    }

    @CircuitBreaker(name = "hotelReservationSystem", fallbackMethod = "handleDeleteFallback")
    public DeleteCardResponse deleteCard(DeleteCardRequest request) {
        return cardServiceBlockingStub.deleteCard(request);
    }

    public DeleteCardResponse handleDeleteFallback(Exception e) {
        log.error("Circuit Breaker aktivdir (DeleteCardResponse). Səbəb: {}", e.getMessage());
        return DeleteCardResponse.newBuilder()
                .setId(-1L)
                .build();
    }
}