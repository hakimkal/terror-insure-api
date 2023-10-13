package com.terron.dto;

import com.terron.models.company.CompanyType;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
public class OnboardCompanyDto {

    private String officialEmailAddress;
    private String companyName;
    @Enumerated(value = EnumType.ORDINAL)
    private CompanyType companyType;
    private String branch;
    private String contactPersonCountryCode;
    private String companyLogo;
    private String contactPersonPhoneNumber;
    @Column(unique = true)
    private String cacNumber;
    private String cacRegistrationDate;
    private String state;
    private String lga;
    private String address;
    private String gpsCoordinate;
    private String contactPersonFirstname;
    private String contactPersonLastname;
}
