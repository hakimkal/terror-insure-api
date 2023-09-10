package com.terron.securityDto;


import com.terron.models.user.Users;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseDto {

    private Users user;

    private String token;
}
