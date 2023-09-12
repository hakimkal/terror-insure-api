package com.terron.models.company;

import com.terron.models.user.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "company_sequence"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    private Long id;

    @Column(unique = true)
    private String officialEmailAddress;
    private String companyName;
    @Enumerated(value = EnumType.ORDINAL)
    private CompanyType companyType;
    private String contactPersonCountryCode;
    private String companyLogo;
    private String branch;
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
