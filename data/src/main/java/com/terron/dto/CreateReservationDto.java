package com.terron.dto;

import com.terron.models.hotels.DocumentType;
import lombok.Data;

@Data
public class CreateReservationDto {
    private String reservationNumber;
    private String lastName;
    private String firstName;
    private String gender;
    private String phoneNumber;
    private String emailAddress;
    private String dateOfArrival;
    private String dateOfDeparture;
    private String noOfRooms;
    private String noOfPersons;
    private String noOfNights;
    private String roomNumber;
    private String roomType;
    private String countryOfDeparture;
    private DocumentType idDocument;
    private String idDocumentNumber;
    private Long companyId;
}
