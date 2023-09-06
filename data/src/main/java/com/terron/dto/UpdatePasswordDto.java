package com.terron.dto;

import lombok.Data;

@Data
public class UpdatePasswordDto {

    private String emailAddress;

    private String password;

    private String newPassword;
}
