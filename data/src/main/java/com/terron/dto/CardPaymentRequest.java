package com.terron.dto;

import lombok.Data;

@Data
public class CardPaymentRequest {
    private String reference;
    private double amount;
    private String cardName;
    private String cardNumber;
    private String cardCvv;
    private String cardExpiryMonth;
    private String cardExpiryYear;
    private String currency;
    private String customerName;
    private String customerEmail;
}

