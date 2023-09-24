package com.terron.services.hotels;

import com.terron.dto.CreateGuestInsuranceDto;
import com.terron.dto.CreateReservationDto;
import com.terron.exceptions.UserAlreadyExistException;
import com.terron.models.hotels.GuestInsurance;
import com.terron.models.hotels.Reservations;
import com.terron.repository.company.CompanyRepository;
import com.terron.repository.hotels.GuestInsuranceRepository;
import com.terron.repository.hotels.ReservationsRepository;
import com.terron.services.utils.HotelPaginationModel;
import com.terron.services.utils.PaginationModel;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@Service
@Slf4j
public class HotelsServiceImpl implements HotelsService{

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ReservationsRepository reservationsRepository;

    @Autowired
    GuestInsuranceRepository guestInsuranceRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Override
    public Reservations createReservation(CreateReservationDto createReservationDto, Long companyId) throws UserAlreadyExistException {
        boolean companyExists = companyRepository.existsById(companyId);
        if (!companyExists) {
            throw new UserAlreadyExistException(String.format("Company with id: %s does not exist exists", companyId));

        }
        Reservations reservation = Reservations.builder()
                .reservationNumber(UUID.randomUUID().toString())
                .companyId(companyId)
                .lastName(createReservationDto.getLastName())
                .firstName(createReservationDto.getFirstName())
                .gender(createReservationDto.getGender())
                .phoneNumber(createReservationDto.getPhoneNumber())
                .emailAddress(createReservationDto.getEmailAddress())
                .dateOfArrival(createReservationDto.getDateOfArrival())
                .dateOfDeparture(createReservationDto.getDateOfDeparture())
                .noOfRooms(createReservationDto.getNoOfRooms())
                .noOfPersons(createReservationDto.getNoOfPersons())
                .noOfNights(createReservationDto.getNoOfNights())
                .roomNumber(createReservationDto.getRoomNumber())
                .roomType(createReservationDto.getRoomType())
                .countryOfDeparture(createReservationDto.getCountryOfDeparture())
                .idDocument(createReservationDto.getIdDocument())
                .idDocumentNumber(createReservationDto.getIdDocumentNumber())
                .createdDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
                .build();
        reservation = reservationsRepository.save(reservation);
        return reservation;
    }

    @Override
    public GuestInsurance createGuestInsurance(CreateGuestInsuranceDto createGuestInsuranceDto, Long companyId) throws UserAlreadyExistException {
        boolean companyExists = companyRepository.existsById(companyId);
        if (!companyExists) {
            throw new UserAlreadyExistException(String.format("Company with id: %s does not exist exists", companyId));

        }
        String token = UUID.randomUUID().toString();

        GuestInsurance guestInsurance = GuestInsurance.builder()
                .certificateNumber(token)
                .companyId(companyId)
                .lastName(createGuestInsuranceDto.getLastName())
                .firstName(createGuestInsuranceDto.getFirstName())
                .gender(createGuestInsuranceDto.getGender())
                .dateOfBirth(createGuestInsuranceDto.getDateOfBirth())
                .state(createGuestInsuranceDto.getState())
                .address(createGuestInsuranceDto.getAddress())
                .phoneNumber(createGuestInsuranceDto.getPhoneNumber())
                .emailAddress(createGuestInsuranceDto.getEmailAddress())
                .nextOfKinPhoneNumber(createGuestInsuranceDto.getNextOfKinPhoneNumber())
                .dateOfArrival(createGuestInsuranceDto.getDateOfArrival())
                .dateOfDeparture(createGuestInsuranceDto.getDateOfDeparture())
                .noOfRooms(createGuestInsuranceDto.getNoOfRooms())
                .noOfPersons(createGuestInsuranceDto.getNoOfPersons())
                .noOfNights(createGuestInsuranceDto.getNoOfNights())
                .roomNumber(createGuestInsuranceDto.getRoomNumber())
                .roomType(createGuestInsuranceDto.getRoomType())
                .arrivalFrom(createGuestInsuranceDto.getArrivalFrom())
                .modeOfPayment(createGuestInsuranceDto.getModeOfPayment())
                .countryOfOrigin(createGuestInsuranceDto.getCountryOfOrigin())
                .nin(createGuestInsuranceDto.getNin())
                .bvn(createGuestInsuranceDto.getBvn())
                .idDocument(createGuestInsuranceDto.getIdDocument())
                .idDocumentNumber(createGuestInsuranceDto.getIdDocumentNumber())
                .height(createGuestInsuranceDto.getHeight())
                .complexion(createGuestInsuranceDto.getComplexion())
                .facialMarks(createGuestInsuranceDto.getFacialMarks())
                .createdDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
                .build();
        guestInsurance = guestInsuranceRepository.save(guestInsurance);
        return guestInsurance;
    }

    @Override
    public PaginationModel getAllReservations(Integer page, Integer pageSize, String searchField, String country, Long companyId) {
        Page<Reservations> reservations = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "createdDate"));
        try {
            reservations = searchField.length() > 0
                    ? reservationsRepository.findByCompanyIdAndFirstNameContainingOrLastNameContainingOrReservationNumberContaining(companyId, searchField, searchField, searchField, pagination)
                    : country.length() > 0
                    ? reservationsRepository.findAllByCompanyIdAndCountryOfDeparture(companyId,country, pagination)
                    : reservationsRepository.findAllByCompanyId(companyId, pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(reservations.getTotalElements());
            paginationModel.setData(reservations.getContent());

            return paginationModel;
        } finally {
            if (reservations != null && reservations instanceof Closeable) {
                try {
                    ((Closeable) reservations).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public PaginationModel getAllGuestInsurance(Integer page, Integer pageSize, String searchField, Long companyId) {
        Page<GuestInsurance> guestInsurances = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "createdDate"));
        try {
            guestInsurances = searchField.length() > 0
                    ? guestInsuranceRepository.findByCompanyIdAndFirstNameContainingOrLastNameContainingOrCertificateNumberContaining(companyId,searchField, searchField, searchField, pagination)
                    : guestInsuranceRepository.findAllByCompanyId(companyId, pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(guestInsurances.getTotalElements());
            paginationModel.setData(guestInsurances.getContent());

            return paginationModel;
        } finally {
            if (guestInsurances != null && guestInsurances instanceof Closeable) {
                try {
                    ((Closeable) guestInsurances).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
