package com.github.annarybina.moneytransfer.service;

import com.github.annarybina.moneytransfer.model.TransferRequest;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class LoggingService {

    private static final String LOG_FILE = "transfer.log";
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void logTransfer(TransferRequest request, String operationId, String status) {
        String logEntry = String.format(
                "%s | From: %s | To: %s | Amount: %d %s | Result: %s | OperationID: %s",
                LocalDateTime.now().format(formatter),
                request.getCardFromNumber(),
                request.getCardToNumber(),
                request.getAmount().getValue(),
                request.getAmount().getCurrency(),
                status,
                operationId
        );

        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(logEntry);
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }

        System.out.println("Logged: " + logEntry);
    }
}