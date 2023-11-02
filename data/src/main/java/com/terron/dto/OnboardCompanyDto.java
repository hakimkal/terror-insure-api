package com.terron.dto;

import com.terron.models.company.CompanyType;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class OnboardCompanyDto {

    private String officialEmailAddress;
    private String companyName;
    @Enumerated(value = EnumType.ORDINAL)
    private CompanyType companyType;
    private String branch;
    private Long insuranceCompany;
    private String contactPersonCountryCode;
    private String companyLogo;
    private String contactPersonPhoneNumber;
    @Column(unique = true)
    private String cacNumber;
    private String cacRegistrationDate;
    private String state;
    private String lga;
    @NotBlank
    private String bvn;
    private String address;
    private String gpsCoordinate;
    private String contactPersonFirstname;
    private String contactPersonLastname;
}
