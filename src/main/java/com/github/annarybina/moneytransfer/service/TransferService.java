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

        // Проверка на одинаковые карты
        if (request.getCardFromNumber().equals(request.getCardToNumber())) {
            return createErrorResponse(request, "4001", "Cannot transfer to the same card", "FAILED: Same card");
        }

        // Проверка на отрицательную или нулевую сумму
        if (request.getAmount().getValue() <= 0) {
            return createErrorResponse(request, "4002", "Amount must be positive", "FAILED: Invalid amount");
        }

        // Проверка на слишком большую сумму (опционально)
        if (request.getAmount().getValue() > 1_000_000_00) { // 1 миллион рублей
            return createErrorResponse(request, "4003", "Amount exceeds maximum limit", "FAILED: Amount too large");
        }

        // Успешный перевод
        TransferResponse response = new TransferResponse();
        response.setOperationId(UUID.randomUUID().toString());
        response.setCode("0000");
        response.setMessage("Success");

        // Расчет комиссии (1%)
        double commission = request.getAmount().getValue() * 0.01;
        loggingService.logTransfer(request, response.getOperationId(), "SUCCESS", commission);

        System.out.println("Transfer completed: " + response.getOperationId());

        return response;
    }

    private TransferResponse createErrorResponse(TransferRequest request, String code, String message, String logStatus) {
        TransferResponse response = new TransferResponse();
        response.setOperationId(UUID.randomUUID().toString());
        response.setCode(code);
        response.setMessage(message);

        loggingService.logTransfer(request, response.getOperationId(), logStatus, 0.0);

        System.out.println("Transfer failed: " + message);
        return response;
    }
}