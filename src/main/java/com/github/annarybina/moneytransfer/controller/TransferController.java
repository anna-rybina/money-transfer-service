package com.github.annarybina.moneytransfer.controller;

import com.github.annarybina.moneytransfer.exception.InvalidInputException;
import com.github.annarybina.moneytransfer.exception.TransferException;
import com.github.annarybina.moneytransfer.model.*;
import com.github.annarybina.moneytransfer.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransferRequest request) {
        try {
            String operationId = transferService.initiateTransfer(request);
            return ResponseEntity.ok(new SuccessResponse(operationId));

        } catch (InvalidInputException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage(), e.getErrorId()));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Internal server error", 5000));
        }
    }

    @PostMapping("/confirmOperation")
    public ResponseEntity<?> confirmOperation(@Valid @RequestBody ConfirmRequest request) {
        try {
            String operationId = transferService.confirmOperation(
                    request.getOperationId(),
                    request.getCode()
            );
            return ResponseEntity.ok(new SuccessResponse(operationId));

        } catch (InvalidInputException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage(), e.getErrorId()));

        } catch (TransferException e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse(e.getMessage(), e.getErrorId()));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Internal server error", 5000));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Money Transfer Service is UP!");
    }
}