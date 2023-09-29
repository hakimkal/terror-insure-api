package com.terron.dto;

import com.terron.models.hotels.DocumentType;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class MissingPersonsDto {

    private String lastName;
    private String firstName;
    private DocumentType idDocument;
    private String idDocumentNumber;
    private String nationality;
    private String reasonForInterest;
    private String remark;
    private String alertContact;
}
