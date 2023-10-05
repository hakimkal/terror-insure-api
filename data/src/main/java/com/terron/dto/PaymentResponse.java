package com.terron.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private String responseCode;
    private boolean success;
    private Object data;
    private String responseMessage;
}

