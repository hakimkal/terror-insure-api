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
public class AlertContactDto {

    private String lastName;
    private String firstName;
    private DocumentType organization;
    private String jobTitle;
    private String phoneNumer;
    private String email;
    private String createdDate;
}
