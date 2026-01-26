package com.example.hotelreservationsystem.service;

import com.example.banksystem.proto.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;


@Service
public class CardGrpcClientService {

    @GrpcClient("cardService")
    private CardServiceGrpc.CardServiceBlockingStub cardServiceBlockingStub;

    public CardResponse getCardByUserId(GetCardByUserIdRequest getCardByUserIdRequest) {
        return cardServiceBlockingStub.getCardByUserId(getCardByUserIdRequest);
    }

    public CardResponse createCard(CreateCardRequest createCardRequest) {
        return cardServiceBlockingStub.createCard(createCardRequest);
    }

    public DeleteCardResponse deleteCard(DeleteCardRequest deleteCardRequest) {
        return cardServiceBlockingStub.deleteCard(deleteCardRequest);
    }

    public CardResponse getCardById(GetCardByIdRequest request){
        return cardServiceBlockingStub.getCardById(request);
    }
    public CardResponse increaseCardBalance(IncreaseCardBalanceRequest increaseCardBalanceRequest){
        return cardServiceBlockingStub.increaseCardBalance(increaseCardBalanceRequest);
    }
    public CardResponse decreaseCardBalance(DecreaseCardBalanceRequest decreaseCardBalanceRequest){
        return cardServiceBlockingStub.decreaseCardBalance(decreaseCardBalanceRequest);
    }




}
