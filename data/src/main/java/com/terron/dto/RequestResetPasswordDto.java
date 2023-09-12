package com.terron.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class RequestResetPasswordDto {

    @NotEmpty
    private String emailAddress;
}
