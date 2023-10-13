package com.terron.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VirtualAccountResponse {
    @JsonProperty("responseCode")
    private String responseCode;

    @JsonProperty("responseMessage")
    private String responseMessage;

    @JsonProperty("data")
    private Data data;

    @JsonProperty("success")
    private boolean success;



    @lombok.Data
    public static class Data {
        @JsonProperty("id")
        private String id;

        @JsonProperty("accountNumber")
        private String accountNumber;

        @JsonProperty("accountName")
        private String accountName;

        @JsonProperty("gateway")
        private String gateway;

        @JsonProperty("accountType")
        private String accountType;

        @JsonProperty("bankName")
        private String bankName;

        @JsonProperty("email")
        private String email;

        @JsonProperty("phone")
        private String phone;

        @JsonProperty("active")
        private boolean active;

        @JsonProperty("deleted")
        private boolean deleted;

        @JsonProperty("creationDate")
        private String creationDate;
    }
}

