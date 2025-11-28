package com.github.annarybina.moneytransfer.service;

import com.github.annarybina.moneytransfer.model.TransferRequest;
import com.github.annarybina.moneytransfer.model.TransferResponse;
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
    void testTransferSuccess() {
        // given
        TransferRequest request = createValidRequest();

        // when
        TransferResponse response = transferService.transfer(request);

        // then
        assertNotNull(response);
        assertEquals("0000", response.getCode());
        assertEquals("Success", response.getMessage());
        assertNotNull(response.getOperationId());

        // Проверяем что комиссия 1% от 10000 = 100.0
        verify(loggingService, times(1))
                .logTransfer(eq(request), anyString(), eq("SUCCESS"), eq(100.0));
    }

    @Test
    void testTransferSameCards() {
        // given
        TransferRequest request = createValidRequest();
        request.setCardToNumber(request.getCardFromNumber()); // одинаковые карты

        // when
        TransferResponse response = transferService.transfer(request);

        // then
        assertNotNull(response);
        assertEquals("4001", response.getCode());
        assertTrue(response.getMessage().toLowerCase().contains("same"));

        verify(loggingService, times(1))
                .logTransfer(eq(request), anyString(), contains("FAILED"), eq(0.0));
    }

    @Test
    void testTransferNegativeAmount() {
        // given
        TransferRequest request = createValidRequest();
        request.getAmount().setValue(-100); // отрицательная сумма

        // when
        TransferResponse response = transferService.transfer(request);

        // then
        assertNotNull(response);
        assertEquals("4002", response.getCode());
        assertTrue(response.getMessage().toLowerCase().contains("positive"));

        verify(loggingService, times(1))
                .logTransfer(eq(request), anyString(), contains("FAILED"), eq(0.0));
    }

    @Test
    void testTransferZeroAmount() {
        // given
        TransferRequest request = createValidRequest();
        request.getAmount().setValue(0); // нулевая сумма

        // when
        TransferResponse response = transferService.transfer(request);

        // then
        assertNotNull(response);
        assertEquals("4002", response.getCode());
        assertTrue(response.getMessage().toLowerCase().contains("positive"));

        verify(loggingService, times(1))
                .logTransfer(eq(request), anyString(), contains("FAILED"), eq(0.0));
    }

    @Test
    void testTransferLargeAmount() {
        // given
        TransferRequest request = createValidRequest();
        request.getAmount().setValue(1_000_000_01); // больше 1 миллиона

        // when
        TransferResponse response = transferService.transfer(request);

        // then
        assertNotNull(response);
        assertEquals("4003", response.getCode());
        assertTrue(response.getMessage().toLowerCase().contains("limit") ||
                response.getMessage().toLowerCase().contains("maximum"));

        verify(loggingService, times(1))
                .logTransfer(eq(request), anyString(), contains("FAILED"), eq(0.0));
    }

    @Test
    void testTransferCommissionCalculation() {
        // given
        TransferRequest request = createValidRequest();
        request.getAmount().setValue(5000); // 5000 рублей

        // when
        TransferResponse response = transferService.transfer(request);

        // then
        assertNotNull(response);
        assertEquals("0000", response.getCode());

        // Проверяем что комиссия 1% от 5000 = 50.0
        verify(loggingService, times(1))
                .logTransfer(eq(request), anyString(), eq("SUCCESS"), eq(50.0));
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