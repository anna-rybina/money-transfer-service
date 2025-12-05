package com.github.annarybina.moneytransfer.controller;

import com.github.annarybina.moneytransfer.model.TransferRequest;
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
        TransferRequest request = new TransferRequest();
        when(transferService.initiateTransfer(any()))
                .thenReturn("test-operation-id");

        // when
        ResponseEntity<?> response = transferController.transfer(request);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testHealth() {
        // when
        ResponseEntity<String> response = transferController.health();

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Money Transfer Service is UP!", response.getBody());
    }
}