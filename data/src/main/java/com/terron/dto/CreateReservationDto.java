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
    private int noOfRooms;
    private int noOfPersons;
    private int noOfNights;
    private int roomNumber;
    private String roomType;
    private String countryOfDeparture;
    private DocumentType idDocument;
    private String idDocumentNumber;
    private Long companyId;
    private String groupName;
}
