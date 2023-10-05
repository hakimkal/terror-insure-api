package com.terron.dto;

import lombok.Data;

@Data
public class BankTransferRequest {
    private String accountNumber;
    private String bankName;
    private String accountHolderName;
    private double amount;
    private String currency;

}

