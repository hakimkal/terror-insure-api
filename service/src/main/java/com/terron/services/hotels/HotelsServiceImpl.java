package com.terron.services.hotels;

import com.terron.dto.CreateGuestInsuranceDto;
import com.terron.dto.CreateReservationDto;
import com.terron.exceptions.UserAlreadyExistException;
import com.terron.models.company.Company;
import com.terron.models.company.CompanyType;
import com.terron.models.hotels.GroupBookings;
import com.terron.models.hotels.GuestInsurance;
import com.terron.models.hotels.Reservations;
import com.terron.models.hotels.ReturnGuests;
import com.terron.models.payment.DailyTransactions;
import com.terron.models.payment.Payment;
import com.terron.models.user.UserRole;
import com.terron.models.user.Users;
import com.terron.repository.company.CompanyRepository;
import com.terron.repository.hotels.GroupBookingsRepository;
import com.terron.repository.hotels.GuestInsuranceRepository;
import com.terron.repository.hotels.ReservationsRepository;
import com.terron.repository.hotels.ReturnGuestRepository;
import com.terron.repository.payment.DailyTransactionRepository;
import com.terron.repository.payment.PaymentRepository;
import com.terron.repository.user.UserRepository;
import com.terron.services.email.EmailService;
import com.terron.services.utils.*;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.usertype.UserType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
@Slf4j
public class HotelsServiceImpl implements HotelsService{

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ReservationsRepository reservationsRepository;

    @Autowired
    ReturnGuestRepository returnGuestRepository;

    @Autowired
    GroupBookingsRepository groupBookingsRepository;

    @Autowired
    GuestInsuranceRepository guestInsuranceRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    DailyTransactionRepository dailyTransactionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TemplateEngine templateEngine;

    @Value("${app.mail.from-address}")
    private String defaultFromAddress;

    @Override
    public void createReservation(CreateReservationDto createReservationDto, Long companyId) throws UserAlreadyExistException {
        boolean companyExists = companyRepository.existsById(companyId);
        if (!companyExists) {
            throw new UserAlreadyExistException(String.format("Company with id: %s does not exist exists", companyId));

        }
        Reservations reservation = null;
        GroupBookings groupBookings = null;
        if(createReservationDto.getNoOfRooms() <= 1){
             reservation = Reservations.builder()
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
             reservationsRepository.save(reservation);
        }
        else {
            groupBookings = GroupBookings.builder()
                    .reservationNumber(UUID.randomUUID().toString())
                    .companyId(companyId)
                    .groupName(createReservationDto.getGroupName())
                    .groupLeader(createReservationDto.getFirstName() + " " + createReservationDto.getLastName())
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
            groupBookingsRepository.save(groupBookings);
        }

        Long isReturnGuest = reservationsRepository.countAllByEmailAddress(createReservationDto.getEmailAddress());

        if(isReturnGuest >= 1){
            createReturnGuest(createReservationDto, companyId);
        }

        try {
            Company hotel = companyRepository.findById(companyId).orElse(null);
            String reservationNumber = createReservationDto.getNoOfRooms() <= 1
                    ? reservation.getReservationNumber()
                    : groupBookings.getReservationNumber();

            Context context = new Context();
            context.setVariable("name", createReservationDto.getFirstName() + " " + createReservationDto.getLastName());
            context.setVariable("reservationNumber", reservationNumber);
            context.setVariable("hotelName", hotel != null ? hotel.getCompanyName() : "");
            context.setVariable("dateOfArrival", createReservationDto.getDateOfArrival());
            context.setVariable("dateOfDeparture", createReservationDto.getDateOfDeparture());
            context.setVariable("roomType", createReservationDto.getRoomType());
            context.setVariable("roomNumber", createReservationDto.getRoomNumber());
            context.setVariable("noOfPersons", createReservationDto.getNoOfPersons());
            context.setVariable("noOfNights", createReservationDto.getNoOfNights());

            String content = templateEngine.process("reservationConfirmation", context);
            emailService.sendNotification(defaultFromAddress, "Terron Reservations",
                    createReservationDto.getEmailAddress(), "Your Reservation is Confirmed", "", content);
        } catch (Exception ex) {
            log.error("Failed to send reservation confirmation email: {}", ex.getMessage());
        }
    }

    public void createReturnGuest(CreateReservationDto createReservationDto, Long companyId){
        ReturnGuests returnGuests = ReturnGuests.builder()
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
        returnGuestRepository.save(returnGuests);
    }

    @Override
    public GuestInsurance createGuestInsurance(CreateGuestInsuranceDto createGuestInsuranceDto, Long companyId) throws UserAlreadyExistException {
        boolean companyExists = companyRepository.existsById(companyId);
        if (!companyExists) {
            throw new UserAlreadyExistException(String.format("Company with id: %s does not exist exists", companyId));

        }
        Company company = companyRepository.findById(companyId).get();
        String token = UUID.randomUUID().toString();
        String token2 = UUID.randomUUID().toString();

        GuestInsurance guestInsurance = GuestInsurance.builder()
                .certificateNumber(token)
                .receiptNumber(token2)
                .companyId(companyId)
                .insuranceCompanyId(company.getInsuranceCompanyId())
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
                .profilePicture(createGuestInsuranceDto.getProfilePicture())
                .verificationDocument(createGuestInsuranceDto.getVerificationDocument())
                .facialMarks(createGuestInsuranceDto.getFacialMarks())
                .createdDate(new Date())
                .build();
        guestInsurance = guestInsuranceRepository.save(guestInsurance);
        Company insuranceCompany = companyRepository.findById(company.getInsuranceCompanyId()).get();

        DailyTransactions dailyTransactions = DailyTransactions.builder()
                .amount(975 * guestInsurance.getNoOfPersons())
                .companyId(companyId)
                .noOfGuest(guestInsurance.getNoOfPersons())
                .transactionDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
                .insuranceCompany(insuranceCompany.getCompanyName())
                .build();
        dailyTransactionRepository.save(dailyTransactions);

        try {
            Context context = new Context();
            context.setVariable("name", createGuestInsuranceDto.getFirstName() + " " + createGuestInsuranceDto.getLastName());
            context.setVariable("certificateNumber", guestInsurance.getCertificateNumber());
            context.setVariable("receiptNumber", guestInsurance.getReceiptNumber());
            context.setVariable("hotelName", company.getCompanyName());
            context.setVariable("dateOfArrival", createGuestInsuranceDto.getDateOfArrival());
            context.setVariable("dateOfDeparture", createGuestInsuranceDto.getDateOfDeparture());
            context.setVariable("roomType", createGuestInsuranceDto.getRoomType());
            context.setVariable("roomNumber", createGuestInsuranceDto.getRoomNumber());
            context.setVariable("noOfPersons", createGuestInsuranceDto.getNoOfPersons());
            context.setVariable("noOfNights", createGuestInsuranceDto.getNoOfNights());

            String content = templateEngine.process("guestInsuranceConfirmation", context);
            emailService.sendNotification(defaultFromAddress, "Terron Check-in",
                    createGuestInsuranceDto.getEmailAddress(), "Your Check-in is Confirmed", "", content);
        } catch (Exception ex) {
            log.error("Failed to send guest insurance confirmation email: {}", ex.getMessage());
        }

        return guestInsurance;
    }

    public void updateGuestInsurances(){
        List<GuestInsurance> guestInsurances = guestInsuranceRepository.findAll();
        for (GuestInsurance guestInsurance: guestInsurances){
            Company company = companyRepository.findById(guestInsurance.getCompanyId()).get();
            if(company.getInsuranceCompanyId() != null){
                guestInsurance.setInsuranceCompanyId(company.getInsuranceCompanyId());
                guestInsuranceRepository.save(guestInsurance);
            }
        }
    }

    public void updateCompanies(){
        List<Company> companies = companyRepository.findAllByCompanyType(CompanyType.hotel);
        for (Company company: companies){
            if(company.getInsuranceCompanyId() == null){
                company.setInsuranceCompanyId(1L);
                companyRepository.save(company);
            }
        }
    }

    @Override
    public PaginationModel getAllReservations(Integer page, Integer pageSize, String searchField, String country, Long companyId) {
        Page<Reservations> reservations = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        try {
            reservations = searchField.length() > 0
                    ? reservationsRepository.findAllByCompanyIdAndFirstNameContainingOrLastNameContainingOrReservationNumberContaining(companyId, searchField, searchField, searchField, pagination)
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

    public PaginationModel getAllGroupBookings(Integer page, Integer pageSize, String searchField, String country, Long companyId) {
        Page<GroupBookings> groupBookings = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        try {
            groupBookings = searchField.length() > 0
                    ? groupBookingsRepository.findAllByCompanyIdAndFirstNameContainingOrLastNameContainingOrReservationNumberContaining(companyId, searchField, searchField, searchField, pagination)
                    : country.length() > 0
                    ? groupBookingsRepository.findAllByCompanyIdAndCountryOfDeparture(companyId,country, pagination)
                    : groupBookingsRepository.findAllByCompanyId(companyId, pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(groupBookings.getTotalElements());
            paginationModel.setData(groupBookings.getContent());

            return paginationModel;
        } finally {
            if (groupBookings != null && groupBookings instanceof Closeable) {
                try {
                    ((Closeable) groupBookings).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public PaginationModel getAllReturnGuest(Integer page, Integer pageSize, String searchField, String country, Long companyId) {
        Page<ReturnGuests> returnGuests = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        try {
            returnGuests = searchField.length() > 0
                    ? returnGuestRepository.findAllByCompanyIdAndFirstNameContainingOrLastNameContainingOrReservationNumberContaining(companyId, searchField, searchField, searchField, pagination)
                    : country.length() > 0
                    ? returnGuestRepository.findAllByCompanyIdAndCountryOfDeparture(companyId,country, pagination)
                    : returnGuestRepository.findAllByCompanyId(companyId, pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(returnGuests.getTotalElements());
            paginationModel.setData(returnGuests.getContent());

            return paginationModel;
        } finally {
            if (returnGuests != null && returnGuests instanceof Closeable) {
                try {
                    ((Closeable) returnGuests).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public double getHotelOutstandingBalance(Long companyId) throws NotFoundException {
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new NotFoundException(String.format("Company with this id: %s does not exist", companyId)));
        List<DailyTransactions> dailyTransactions = dailyTransactionRepository.findAllByCompanyId(company.getId());
        List<Payment> payments = paymentRepository.findAllByCompanyId(company.getId());

        double totalPaymentAmount = payments.stream()
                .mapToDouble(Payment::getAmountPaid)
                .sum();

        double totalTransactionAmount = dailyTransactions.stream()
                .mapToDouble(DailyTransactions::getAmount)
                .sum();

        return totalTransactionAmount - totalPaymentAmount;
    }

    @Override
    public PaginationModel getAllGuestInsurance(Integer page, Integer pageSize, String searchField, Long companyId) {
        Page<GuestInsurance> guestInsurances = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
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

    @Override
    public PaginationModel getAllUsers(Integer page, Integer pageSize, String searchField, Long companyId) {
        Page<Users> users = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "registeredDate"));
        try {
            users = searchField.length() > 0
                    ? userRepository.findByCompanyIdAndFirstNameContainingOrLastNameContainingOrPhoneNumberContaining(companyId, searchField, searchField, searchField, pagination)
                    : userRepository.findAllByCompanyId(companyId, pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(users.getTotalElements());
            paginationModel.setData(users.getContent());

            return paginationModel;
        } finally {
            if (users != null && users instanceof Closeable) {
                try {
                    ((Closeable) users).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public PaginationModel getAllUsersByCompanyType(Integer page, Integer pageSize, String searchField, UserRole userType) {
        Page<Users> users = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "registeredDate"));
        try {
            users = searchField.length() > 0
                    ? userRepository.findByRoleAndFirstNameContainingOrLastNameContainingOrPhoneNumberContaining(userType, searchField, searchField, searchField, pagination)
                    : userRepository.findAllByRole(userType, pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(users.getTotalElements());
            paginationModel.setData(users.getContent());

            return paginationModel;
        } finally {
            if (users != null && users instanceof Closeable) {
                try {
                    ((Closeable) users).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public CompanyPaginatedModel getSingleCompany(Long companyId) throws UserAlreadyExistException {
        Company company = companyRepository.findById(companyId).orElseThrow( () -> new UserAlreadyExistException(String.format("Company with id: %s does not exist", companyId)));
        Long totalGuest = guestInsuranceRepository.countAllByCompanyId(companyId);
        Long totalReservations = reservationsRepository.countAllByCompanyId(companyId);
        float totalReservationsPercentage = (totalReservations / (float) totalGuest) * 100;
        List<Payment> payments = paymentRepository.findAllByCompanyId(companyId);
        double totalAmountPaid = payments.stream()
                .mapToDouble(Payment::getAmountPaid)
                .sum();

        List<GuestInsurance> guestInsurances = guestInsuranceRepository.findAllByCompanyId(companyId);
        Map<String, Integer> emailCounts = new HashMap<>();

        for (GuestInsurance guestInsurance : guestInsurances) {
            String email = guestInsurance.getEmailAddress();
            if (emailCounts.containsKey(email)) {
                emailCounts.put(email, emailCounts.get(email) + 1);
            } else {
                emailCounts.put(email, 1);
            }
        }

        int uniqueEmailCount = 0;
        int duplicateEmailCount = 0;
        for (Map.Entry<String, Integer> entry : emailCounts.entrySet()) {
            if (entry.getValue() == 1) {
                uniqueEmailCount++;
            } else {
                duplicateEmailCount++;
            }
        }
        double totalAmountInsured = totalGuest * 690.0;
        CompanyPaginatedModel companyPaginatedModel = new CompanyPaginatedModel();
        companyPaginatedModel.setData(company);
        companyPaginatedModel.setTotalGuest(totalGuest);
        companyPaginatedModel.setNewGuest(uniqueEmailCount);
        companyPaginatedModel.setReturnGuest(duplicateEmailCount);
        companyPaginatedModel.setTotalReservations(totalReservations);
        companyPaginatedModel.setTotalReservationsPercentage(totalReservationsPercentage);
        companyPaginatedModel.setTotalAmountInsured(totalAmountInsured);
        companyPaginatedModel.setAmountPaid(totalAmountPaid);
        companyPaginatedModel.setOutstandingAmount(totalAmountInsured - totalAmountPaid);
        return companyPaginatedModel;
    }


    public PaginationModel getReservations(Integer page, Integer pageSize, String searchField, String country, String filter) {
        Page<Reservations> reservations = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        Company company = new Company();
        if(filter != null && !filter.isEmpty()){
            company = companyRepository.findByCompanyName(filter);
        }
        try {
            reservations = searchField.length() > 0
                    ? reservationsRepository.findByFirstNameContainingOrLastNameContainingOrReservationNumberContaining(searchField, searchField, searchField, pagination)
                    : country.length() > 0
                    ? reservationsRepository.findAllByCountryOfDeparture(country, pagination)
                    : filter.length() > 0
                    ? reservationsRepository.findAllByCompanyId(company.getId(), pagination)
                    : reservationsRepository.findAll(pagination);

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

    public PaginationModel getGroupBookings(Integer page, Integer pageSize, String searchField, String country, String filter) {
        Page<GroupBookings> groupBookings = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        Company company = new Company();
        if(filter != null && !filter.isEmpty()){
            company = companyRepository.findByCompanyName(filter);
        }
        try {
            groupBookings = searchField.length() > 0
                    ? groupBookingsRepository.findAllByFirstNameContainingOrLastNameContainingOrReservationNumberContaining(searchField, searchField, searchField, pagination)
                    : country.length() > 0
                    ? groupBookingsRepository.findAllByCountryOfDeparture(country, pagination)
                    : filter.length() > 0
                    ? groupBookingsRepository.findAllByCompanyId(company.getId(), pagination)
                    : groupBookingsRepository.findAll(pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(groupBookings.getTotalElements());
            paginationModel.setData(groupBookings.getContent());

            return paginationModel;
        } finally {
            if (groupBookings != null && groupBookings instanceof Closeable) {
                try {
                    ((Closeable) groupBookings).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public PaginationModel getGuestInsurances(Integer page, Integer pageSize, String searchField, String filter) {
        Page<GuestInsurance> guestInsurances = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        Company company = new Company();
        if(filter != null && !filter.isEmpty()){
            company = companyRepository.findByCompanyName(filter);
        }
        try {
            guestInsurances = searchField.length() > 0
                    ? guestInsuranceRepository.findByFirstNameContainingOrLastNameContainingOrCertificateNumberContaining(searchField, searchField, searchField, pagination)
                    : filter.length() > 0
                    ? guestInsuranceRepository.findAllByCompanyId(company.getId(), pagination)
                    : guestInsuranceRepository.findAll(pagination);

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

    public ReservationDetailsDto getSingleReservations(Long reservationId) throws UserAlreadyExistException {
        Reservations reservation = reservationsRepository.findById(reservationId).
                orElseThrow( () -> new UserAlreadyExistException(String.format("Reservation with id: %s does not exist", reservationId)));
        Company company = companyRepository.findById(reservation.getCompanyId()).get();
        return ReservationDetailsDto.builder()
                .reservation(reservation)
                .company(company)
                .build();
    }

    public GroupBookingDetailsDto getSingleGroupBooking(Long groupBookingId) throws UserAlreadyExistException {
        GroupBookings groupBooking = groupBookingsRepository.findById(groupBookingId).
                orElseThrow( () -> new UserAlreadyExistException(String.format("GroupBooking with id: %s does not exist", groupBookingId)));
        Company company = companyRepository.findById(groupBooking.getCompanyId()).get();
        return GroupBookingDetailsDto.builder()
                .groupBooking(groupBooking)
                .company(company)
                .build();
    }

    public GuestInsuranceDto getSingleGuestInsurance(Long guestInsuranceId) throws UserAlreadyExistException {
        GuestInsurance guestInsurance = guestInsuranceRepository.findById(guestInsuranceId).
                orElseThrow( () -> new UserAlreadyExistException(String.format("Guest insurance with id: %s does not exist", guestInsuranceId)));

        Company company = companyRepository.findById(guestInsurance.getCompanyId()).get();
        return GuestInsuranceDto.builder()
                .guestInsurance(guestInsurance)
                .company(company)
                .build();
    }
}
