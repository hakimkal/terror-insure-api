package com.terron.securityDto;

import lombok.Data;

@Data
public class UserLoginRequestDto {
    private String emailAddress;
    private String password;
}
