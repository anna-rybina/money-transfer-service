package com.github.annarybina.moneytransfer.model;

import jakarta.validation.constraints.*;

public class TransferRequest {
    @NotBlank
    @Pattern(regexp = "\\d{16}")
    private String cardFromNumber;

    @NotBlank
    @Pattern(regexp = "(0[1-9]|1[0-2])/[0-9]{2}")
    private String cardFromValidTill;

    @NotBlank
    @Pattern(regexp = "\\d{3}")
    private String cardFromCVV;

    @NotBlank
    @Pattern(regexp = "\\d{16}")
    private String cardToNumber;

    @NotNull
    private Amount amount;

    public String getCardFromNumber() { return cardFromNumber; }
    public void setCardFromNumber(String cardFromNumber) { this.cardFromNumber = cardFromNumber; }

    public String getCardFromValidTill() { return cardFromValidTill; }
    public void setCardFromValidTill(String cardFromValidTill) { this.cardFromValidTill = cardFromValidTill; }

    public String getCardFromCVV() { return cardFromCVV; }
    public void setCardFromCVV(String cardFromCVV) { this.cardFromCVV = cardFromCVV; }

    public String getCardToNumber() { return cardToNumber; }
    public void setCardToNumber(String cardToNumber) { this.cardToNumber = cardToNumber; }

    public Amount getAmount() { return amount; }
    public void setAmount(Amount amount) { this.amount = amount; }

    public static class Amount {
        @NotNull
        @Positive
        private Integer value;

        @NotBlank
        private String currency;

        public Integer getValue() { return value; }
        public void setValue(Integer value) { this.value = value; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }
}