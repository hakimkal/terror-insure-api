package com.terron.dto;

import com.terron.models.user.UserRole;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateProfileDto {

    private String emailAddress;

    private String phoneNumber;

    private String profilePic;

    private UserRole role;

    private Long companyId;

    private String firstName;

    private String lastName;

    private String staffNo;

    private String govtAgency;

}
