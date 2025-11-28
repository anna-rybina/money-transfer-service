package com.github.annarybina.moneytransfer.service;

import com.github.annarybina.moneytransfer.model.TransferRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoggingServiceTest {

    @Test
    void testLogTransferSuccess() throws Exception {
        // given
        LoggingService loggingService = new LoggingService();
        TransferRequest request = createValidRequest();
        String operationId = "test-operation-id";

        // when
        loggingService.logTransfer(request, operationId, "SUCCESS", 100.0);

        // then
        File logFile = new File("transfer.log");
        assertTrue(logFile.exists());

        // Проверяем содержимое файла
        List<String> lines = Files.readAllLines(logFile.toPath());
        assertFalse(lines.isEmpty());
        String lastLine = lines.get(lines.size() - 1);
        assertTrue(lastLine.contains("SUCCESS"));
        assertTrue(lastLine.contains("100")); // Проверяем только число, без формата
        assertTrue(lastLine.contains(operationId));
    }

    @Test
    void testLogTransferFailed() throws Exception {
        // given
        LoggingService loggingService = new LoggingService();
        TransferRequest request = createValidRequest();
        String operationId = "test-failed-operation";

        // when
        loggingService.logTransfer(request, operationId, "FAILED: Same card", 0.0);

        // then
        File logFile = new File("transfer.log");
        assertTrue(logFile.exists());

        List<String> lines = Files.readAllLines(logFile.toPath());
        String lastLine = lines.get(lines.size() - 1);
        assertTrue(lastLine.contains("FAILED"));
        assertTrue(lastLine.contains("Same card"));
        assertTrue(lastLine.contains(operationId));
    }

    @Test
    void testLogTransferCreatesFile() {
        // given
        LoggingService loggingService = new LoggingService();
        TransferRequest request = createValidRequest();

        // when
        loggingService.logTransfer(request, "test-id", "TEST", 50.0);

        // then
        File logFile = new File("transfer.log");
        assertTrue(logFile.exists());
    }

    @Test
    void testLogTransferAppendsToFile() throws Exception {
        // given
        LoggingService loggingService = new LoggingService();
        TransferRequest request = createValidRequest();

        // when - пишем две записи
        loggingService.logTransfer(request, "first-id", "FIRST", 10.0);
        int linesAfterFirst = Files.readAllLines(new File("transfer.log").toPath()).size();

        loggingService.logTransfer(request, "second-id", "SECOND", 20.0);

        // then - файл должен содержать обе записи
        List<String> lines = Files.readAllLines(new File("transfer.log").toPath());
        assertEquals(linesAfterFirst + 1, lines.size());
        assertTrue(lines.get(lines.size() - 1).contains("second-id"));
    }

    private TransferRequest createValidRequest() {
        TransferRequest request = new TransferRequest();
        request.setCardFromNumber("1234567812345678");
        request.setCardFromValidTill("12/25");
        request.setCardFromCVV("123");
        request.setCardToNumber("8765432187654321");

        TransferRequest.Amount amount = new TransferRequest.Amount();
        amount.setValue(10000);
        amount.setCurrency("RUB");
        request.setAmount(amount);

        return request;
    }
}
