package com.terron.dto;

import com.terron.models.hotels.DocumentType;
import com.terron.models.hotels.PaymentMode;
import lombok.Data;

@Data
public class CreateGuestInsuranceDto {

    private String certificateNumber;
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
    private int noOfRooms;
    private int noOfPersons;
    private int noOfNights;
    private int roomNumber;
    private String roomType;
    private String arrivalFrom;
    private PaymentMode modeOfPayment;
    private String countryOfOrigin;
    private String nin;
    private String bvn;
    private DocumentType idDocument;
    private String idDocumentNumber;
    private String profilePicture;
    private String verificationDocument;
    private String height;
    private String complexion;
    private String facialMarks;
    private Long companyId;
}
