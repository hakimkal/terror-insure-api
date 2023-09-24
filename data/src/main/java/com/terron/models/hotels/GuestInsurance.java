package com.terron.models.hotels;

import com.terron.models.company.CompanyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuestInsurance {

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "guest_insurance_sequence"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    private Long id;
    private String certificateNumber;
    private Long companyId;
    private String lastName;
    private String firstName;
    private String gender;
    private String dateOfBirth;
    private String state;
    private String address;
    private String phoneNumber;
    private String emailAddress;
    private String nextOfKinPhoneNumber;
    private String dateOfArrival;
    private String dateOfDeparture;
    private String noOfRooms;
    private String noOfPersons;
    private String noOfNights;
    private String roomNumber;
    private String roomType;
    private String arrivalFrom;
    private PaymentMode modeOfPayment;
    private String countryOfOrigin;
    private String nin;
    private String bvn;
    private DocumentType idDocument;
    private String idDocumentNumber;
    private String height;
    private String complexion;
    private String facialMarks;
    private String createdDate;
}

