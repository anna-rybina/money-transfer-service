package com.github.annarybina.moneytransfer.model;

public class TransferResponse {
    private String operationId;
    private String code;
    private String message;

    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}