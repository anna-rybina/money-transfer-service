package com.github.annarybina.moneytransfer.service;

import com.github.annarybina.moneytransfer.model.TransferRequest;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${logging.format}")
    private String logFormat;

    public void logTransfer(TransferRequest request, String operationId,
                            String status, double commission) {

        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = logFormat
                .replace("{timestamp}", timestamp)
                .replace("{cardFrom}", maskCardNumber(request.getCardFromNumber()))
                .replace("{cardTo}", maskCardNumber(request.getCardToNumber()))
                .replace("{amount}", String.valueOf(request.getAmount().getValue()))
                .replace("{currency}", request.getAmount().getCurrency())
                .replace("{commission}", String.format("%.2f", commission))
                .replace("{status}", status)
                .replace("{operationId}", operationId);

        writeToFile(logEntry);
        System.out.println("Logged: " + logEntry);
    }

    private void writeToFile(String logEntry) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(logEntry);
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 12) {
            return cardNumber;
        }
        return cardNumber.substring(0, 6) + "******" + cardNumber.substring(12);
    }
}