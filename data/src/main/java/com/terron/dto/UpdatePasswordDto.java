package com.terron.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdatePasswordDto {

    @NotBlank
    private String newPassword;
}
