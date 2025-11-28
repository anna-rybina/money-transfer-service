package com.github.annarybina.moneytransfer.service;

import com.github.annarybina.moneytransfer.model.TransferRequest;
import com.github.annarybina.moneytransfer.model.TransferResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransferService {

    private final LoggingService loggingService;

    public TransferService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    public TransferResponse transfer(TransferRequest request) {
        System.out.println("Processing transfer: " + request.getCardFromNumber() + " -> " + request.getCardToNumber());

        TransferResponse response = new TransferResponse();
        response.setOperationId(UUID.randomUUID().toString());
        response.setCode("0000");
        response.setMessage("Success");

        loggingService.logTransfer(request, response.getOperationId(), "SUCCESS");

        System.out.println("Transfer completed: " + response.getOperationId());

        return response;
    }
}