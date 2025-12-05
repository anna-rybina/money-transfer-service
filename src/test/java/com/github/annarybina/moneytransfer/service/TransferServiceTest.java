package com.github.annarybina.moneytransfer.service;

import com.github.annarybina.moneytransfer.exception.InvalidInputException;
import com.github.annarybina.moneytransfer.model.TransferRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private TransferService transferService;

    @Test
    void testInitiateTransferSuccess() {
        // given
        TransferRequest request = createValidRequest();

        // when
        String operationId = transferService.initiateTransfer(request);

        // then
        assertNotNull(operationId);
        verify(loggingService, times(1))
                .logTransfer(eq(request), eq(operationId), eq("PENDING"), eq(0.0));
    }

    @Test
    void testInitiateTransferSameCard() {
        // given
        TransferRequest request = createValidRequest();
        request.setCardToNumber(request.getCardFromNumber());

        // when & then
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> transferService.initiateTransfer(request));
        assertEquals(4001, exception.getErrorId());
        assertEquals("Cannot transfer to the same card", exception.getMessage());
    }

    @Test
    void testConfirmOperationSuccess() {
        // given
        TransferRequest request = createValidRequest();
        String operationId = transferService.initiateTransfer(request);

        // when
        String confirmedId = transferService.confirmOperation(operationId, "0000");

        // then
        assertEquals(operationId, confirmedId);
        verify(loggingService, times(1))
                .logTransfer(eq(request), eq(operationId), eq("SUCCESS"), eq(100.0));
    }

    @Test
    void testConfirmOperationInvalidCode() {
        // given
        TransferRequest request = createValidRequest();
        String operationId = transferService.initiateTransfer(request);

        // when & then
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> transferService.confirmOperation(operationId, "9999"));
        assertEquals(4007, exception.getErrorId());
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