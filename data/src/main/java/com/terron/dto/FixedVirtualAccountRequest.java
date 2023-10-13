package com.terron.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FixedVirtualAccountRequest {
    @JsonProperty("customerName")
    private String customerName;

    @JsonProperty("customerMobile")
    private String customerMobile;

    @JsonProperty("customerEmail")
    private String customerEmail;

    @JsonProperty("bvn")
    private String bvn;

}

