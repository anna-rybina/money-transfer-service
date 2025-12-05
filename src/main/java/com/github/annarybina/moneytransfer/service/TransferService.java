package com.github.annarybina.moneytransfer.service;

import com.github.annarybina.moneytransfer.exception.InvalidInputException;
import com.github.annarybina.moneytransfer.exception.TransferException;
import com.github.annarybina.moneytransfer.model.TransferRequest;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransferService {

    private final LoggingService loggingService;

    private final Map<String, TransferRequest> pendingOperations = new ConcurrentHashMap<>();
    private final Map<String, String> confirmationCodes = new ConcurrentHashMap<>();

    public TransferService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    public String initiateTransfer(TransferRequest request) {
        System.out.println("Initiating transfer: " + request.getCardFromNumber() + " -> " + request.getCardToNumber());

        // Проверка на одинаковые карты
        if (request.getCardFromNumber().equals(request.getCardToNumber())) {
            String errorId = "ERROR-" + UUID.randomUUID().toString();
            loggingService.logTransfer(request, errorId, "FAILED: Same card", 0.0);
            throw new InvalidInputException("Cannot transfer to the same card", 4001);
        }

        // Проверка на отрицательную или нулевую сумму
        if (request.getAmount().getValue() <= 0) {
            String errorId = "ERROR-" + UUID.randomUUID().toString();
            loggingService.logTransfer(request, errorId, "FAILED: Invalid amount", 0.0);
            throw new InvalidInputException("Amount must be positive", 4002);
        }

        // Проверка на слишком большую сумму
        if (request.getAmount().getValue() > 1_000_000_00) {
            String errorId = "ERROR-" + UUID.randomUUID().toString();
            loggingService.logTransfer(request, errorId, "FAILED: Amount exceeds limit", 0.0);
            throw new InvalidInputException("Amount exceeds maximum limit", 4003);
        }

        // Проверка валюты
        if (!"RUB".equalsIgnoreCase(request.getAmount().getCurrency())) {
            String errorId = "ERROR-" + UUID.randomUUID().toString();
            loggingService.logTransfer(request, errorId, "FAILED: Unsupported currency", 0.0);
            throw new InvalidInputException("Only RUB currency is supported", 4004);
        }

        // Проверка срока действия карты
        if (!isCardValid(request.getCardFromValidTill())) {
            String errorId = "ERROR-" + UUID.randomUUID().toString();
            loggingService.logTransfer(request, errorId, "FAILED: Card expired", 0.0);
            throw new InvalidInputException("Card expired or invalid date", 4005);
        }

        // Генерация operationId
        String operationId = UUID.randomUUID().toString();

        // Сохраняем операцию для подтверждения
        pendingOperations.put(operationId, request);

        // Генерируем код подтверждения
        String confirmationCode = generateConfirmationCode();
        confirmationCodes.put(operationId, confirmationCode);

        System.out.println("Generated confirmation code for operation " + operationId + ": " + confirmationCode);

        // Логируем инициацию перевода
        loggingService.logTransfer(request, operationId, "PENDING", 0.0);

        return operationId;
    }

    public String confirmOperation(String operationId, String code) {
        System.out.println("Confirming operation: " + operationId);

        // Проверяем существование операции
        if (!pendingOperations.containsKey(operationId)) {
            throw new InvalidInputException("Operation not found", 4006);
        }

        // Проверяем код подтверждения
        String expectedCode = confirmationCodes.get(operationId);
        if (expectedCode == null || !expectedCode.equals(code)) {
            throw new InvalidInputException("Invalid confirmation code", 4007);
        }

        TransferRequest request = pendingOperations.get(operationId);

        try {
            // Расчет комиссии (1%)
            double commission = request.getAmount().getValue() * 0.01;

            // Логируем успешный перевод
            loggingService.logTransfer(request, operationId, "SUCCESS", commission);

            // Очищаем временные данные
            pendingOperations.remove(operationId);
            confirmationCodes.remove(operationId);

            System.out.println("Transfer confirmed and completed: " + operationId);

            return operationId;

        } catch (Exception e) {
            // Логируем ошибку при переводе
            loggingService.logTransfer(request, operationId, "FAILED: Transfer error", 0.0);
            throw new TransferException("Transfer failed: " + e.getMessage(), 5001);
        }
    }

    private boolean isCardValid(String validTill) {
        try {
            // Формат: MM/YY
            String[] parts = validTill.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]) + 2000; // предполагаем 21 век

            // Простая проверка (в реальном приложении сложнее)
            return month >= 1 && month <= 12 && year >= 2024;
        } catch (Exception e) {
            return false;
        }
    }

    private String generateConfirmationCode() {
        // В реальном приложении это был бы код из СМС
        // Для тестирования используем фиксированный код "0000"
        return "0000";
    }
}