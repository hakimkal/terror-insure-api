package com.terron.models.hotels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBookings {

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "group_bookings_sequence"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    private Long id;
    private String reservationNumber;
    private Long companyId;
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
    private String groupName;
    private String groupLeader;
    private String roomType;
    private String countryOfDeparture;
    private DocumentType idDocument;
    private String idDocumentNumber;
    private String createdDate;
}
