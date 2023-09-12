package com.terron.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChangePasswordDto {

    @NotBlank
    private String password;

    @NotBlank
    private String emailAddress;
}
