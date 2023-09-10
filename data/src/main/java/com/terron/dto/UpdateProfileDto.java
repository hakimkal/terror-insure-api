package com.terron.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateProfileDto {

    @Email
    @NotBlank
    @NotNull
    private String email;

    @Email
    private String newEmail;

    private String firstName;

    private String LastName;

}
