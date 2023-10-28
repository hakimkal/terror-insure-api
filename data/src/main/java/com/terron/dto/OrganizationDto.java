package com.terron.dto;

import com.terron.models.hotels.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Builder
public class OrganizationDto {

    private String organizationType;
    private String organizationName;
    private DocumentType abbreviation;
    private String state;
    private String address;
    private String phoneNumer;
    private String email;
    private String logo;
    private String createdDate;
}
