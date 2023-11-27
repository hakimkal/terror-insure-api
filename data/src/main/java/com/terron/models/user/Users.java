package com.terron.models.user;

import com.terron.models.company.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "user_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private Long id;

    @Column(unique = true)
    private String emailAddress;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String password;

    private String staffNo;

    private String govtAgency;

    String verificationToken;

    @Enumerated(value = EnumType.ORDINAL)
    private UserRole role;

    private String modifiedDate;

    private String registeredDate;

    private Boolean isActive;

    private Boolean isVerified;

    private Long companyId;

    private String profilePic;
}
