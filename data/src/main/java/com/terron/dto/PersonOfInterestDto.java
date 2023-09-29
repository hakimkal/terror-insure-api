package com.terron.dto;

import com.terron.models.hotels.DocumentType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonOfInterestDto {

    private String lastName;
    private String firstName;
    private String gender;
    private String dateOfBirth;
    private String phoneNumber;
    private String emailAddress;
    private DocumentType idDocument;
    private String idDocumentNumber;
    private String nationality;
    private String height;
    private String weight;
    private String heightType;
    private String hairColor;
    private String complexionType;
    private String eyeColor;
    private String reasonForInterest;
    private String remark;
    private String alertContact;
    private String image;
}
