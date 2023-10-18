package com.terron.dto;

import com.terron.models.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {

    @Email
    @NotBlank
    private String emailAddress;
    @NotBlank
    private String password;

    private UserRole role;

    private Long companyId;

    private String firstName;

    private String lastName;

    private String staffNo;

    private String govtAgency;

    private String phoneNumber;
}
