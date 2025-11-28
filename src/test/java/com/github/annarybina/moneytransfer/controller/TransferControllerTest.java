package com.github.annarybina.moneytransfer.controller;

import com.github.annarybina.moneytransfer.model.TransferRequest;
import com.github.annarybina.moneytransfer.model.TransferResponse;
import com.github.annarybina.moneytransfer.service.TransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    @Mock
    private TransferService transferService;

    @InjectMocks
    private TransferController transferController;

    @Test
    void testTransferSuccess() {
        // given
        TransferRequest request = createValidRequest();
        TransferResponse serviceResponse = new TransferResponse();
        serviceResponse.setOperationId("test-op-id");
        serviceResponse.setCode("0000");
        serviceResponse.setMessage("Success");

        when(transferService.transfer(any(TransferRequest.class))).thenReturn(serviceResponse);

        // when
        ResponseEntity<TransferResponse> response = transferController.transfer(request);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test-op-id", response.getBody().getOperationId());
        assertEquals("0000", response.getBody().getCode());
        assertEquals("Success", response.getBody().getMessage());

        verify(transferService, times(1)).transfer(request);
    }

    @Test
    void testHealthEndpoint() {
        // when
        ResponseEntity<String> response = transferController.health();

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Money Transfer Service is UP!", response.getBody());
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
