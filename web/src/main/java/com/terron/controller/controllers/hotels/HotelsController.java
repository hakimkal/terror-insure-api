package com.terron.controller.controllers.hotels;

import static com.terron.utils.utility.decodeToken;

import com.terron.dto.CreateGuestInsuranceDto;
import com.terron.dto.CreateReservationDto;
import com.terron.exceptions.UserAlreadyExistException;
import com.terron.models.hotels.GuestInsurance;
import com.terron.repository.user.UserRepository;
import com.terron.response.ResponseDetails;
import com.terron.response.ResponseDetailsWithObject;
import com.terron.services.company.CompanyServiceImpl;
import com.terron.services.hotels.HotelsServiceImpl;
import com.terron.services.utils.CompanyPaginatedModel;
import com.terron.services.utils.GroupBookingDetailsDto;
import com.terron.services.utils.GuestInsuranceDto;
import com.terron.services.utils.PaginationModel;
import com.terron.services.utils.ReservationDetailsDto;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/v1/hotels")
public class HotelsController {

    @Autowired
    HotelsServiceImpl hotelsService;

    @Autowired
    CompanyServiceImpl companyService;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/reservations/{companyId}")
    public ResponseEntity<?> addReservation(@Valid @RequestBody CreateReservationDto createReservationDto,@PathVariable Long companyId, @RequestHeader(name = "Authorization") String token) throws UserAlreadyExistException {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_COMPANY_OWNER") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        hotelsService.createReservation(createReservationDto, companyId);
        ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Reservation added successfully", "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }

    @GetMapping("/reservations/{companyId}")
    public ResponseEntity<?> getReservations(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "searchField", defaultValue = "", required = false) String searchField,
            @RequestParam(value = "country", defaultValue = "", required = false) String country,
            @RequestHeader(name = "Authorization") String token,
            @PathVariable Long companyId
    ) {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_NTDA") && !Objects.equals(role, "ROLE_COMPANY_OWNER") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        PaginationModel reservations = hotelsService.getAllReservations(page, pageSize, searchField, country, companyId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/group-bookings/{companyId}")
    public ResponseEntity<?> getGroupBookings(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "searchField", defaultValue = "", required = false) String searchField,
            @RequestParam(value = "country", defaultValue = "", required = false) String country,
            @RequestHeader(name = "Authorization") String token,
            @PathVariable Long companyId
    ) {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_COMPANY_OWNER") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        PaginationModel groupBookings = hotelsService.getAllGroupBookings(page, pageSize, searchField, country, companyId);
        return new ResponseEntity<>(groupBookings, HttpStatus.OK);
    }

    @PostMapping("/guest-insurances/{companyId}")
    public ResponseEntity<?> addGuestInsurance(@Valid @RequestBody CreateGuestInsuranceDto createGuestInsuranceDto,@PathVariable Long companyId, @RequestHeader(name = "Authorization") String token) throws UserAlreadyExistException {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_COMPANY_OWNER") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }

        GuestInsurance guestInsurance = hotelsService.createGuestInsurance(createGuestInsuranceDto, companyId);
        ResponseDetailsWithObject responseDetails = new ResponseDetailsWithObject(LocalDateTime.now(), "Guest insurance added successfully", guestInsurance, "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }

    @GetMapping("/update-guest-insurances")
    public ResponseEntity<?> updateGuestInsurance() {
        hotelsService.updateGuestInsurances();
        ResponseDetailsWithObject responseDetails = new ResponseDetailsWithObject(LocalDateTime.now(), "Guest insurance updated successfully", "guestInsurance", "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }

    @GetMapping("/update-companies")
    public ResponseEntity<?> updateCompanies() {
        hotelsService.updateCompanies();
        ResponseDetailsWithObject responseDetails = new ResponseDetailsWithObject(LocalDateTime.now(), "Company updated successfully", "companies", "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }

    @GetMapping("/guest-insurances/{companyId}")
    public ResponseEntity<?> getGuestInsurances(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "searchField", defaultValue = "", required = false) String searchField,
            @RequestHeader(name = "Authorization") String token,
            @PathVariable Long companyId
    ) {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_COMPANY_OWNER") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }

        PaginationModel guestInsurance = hotelsService.getAllGuestInsurance(page, pageSize, searchField, companyId);
        return new ResponseEntity<>(guestInsurance, HttpStatus.OK);
    }

    @GetMapping("/return-guests/{companyId}")
    public ResponseEntity<?> getReturnGuest(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "searchField", defaultValue = "", required = false) String searchField,
            @RequestParam(value = "country", defaultValue = "", required = false) String country,
            @RequestHeader(name = "Authorization") String token,
            @PathVariable Long companyId
    ) {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_COMPANY_OWNER") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }

        PaginationModel guestInsurance = hotelsService.getAllReturnGuest(page, pageSize, searchField, country, companyId);
        return new ResponseEntity<>(guestInsurance, HttpStatus.OK);
    }

    @GetMapping("/users/{companyId}")
    public ResponseEntity<?> getUsers(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "searchField", defaultValue = "", required = false) String searchField,
            @RequestHeader(name = "Authorization") String token,
            @PathVariable Long companyId
    ) {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_COMPANY_OWNER") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }

        PaginationModel users = hotelsService.getAllUsers(page, pageSize, searchField, companyId);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/payments/{companyId}")
    public ResponseEntity<?> getHotelPayment(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "startDate", defaultValue = "", required = false) String startDate,
            @RequestParam(value = "endDate", defaultValue = "", required = false) String endDate,
            @PathVariable Long companyId,
            @RequestHeader(name = "Authorization") String token
    ) {
        String role = decodeToken(token);
        if(!Objects.equals(role, "ROLE_ADMIN")  && !Objects.equals(role, "ROLE_COMPANY_OWNER") && !Objects.equals(role, "ROLE_NTDC")){
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        PaginationModel hotelPayments = companyService.getAllHotelPayments(companyId, startDate, endDate ,page, pageSize);
        return new ResponseEntity<>(hotelPayments, HttpStatus.OK);
    }

    @GetMapping("/transactions/{companyId}")
    public ResponseEntity<?> getHotelTransactions(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "startDate", defaultValue = "", required = false) String startDate,
            @RequestParam(value = "endDate", defaultValue = "", required = false) String endDate,
            @PathVariable Long companyId,
            @RequestHeader(name = "Authorization") String token
    ) {
        String role = decodeToken(token);
        if(!Objects.equals(role, "ROLE_ADMIN")  && !Objects.equals(role, "ROLE_COMPANY_OWNER") && !Objects.equals(role, "ROLE_NTDA") && !Objects.equals(role, "ROLE_NTDC")){
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        PaginationModel hotelTransactions = companyService.getAllHotelTransactions(companyId, startDate, endDate ,page, pageSize);
        return new ResponseEntity<>(hotelTransactions, HttpStatus.OK);
    }

    @GetMapping ("/details/{companyId}")
    public ResponseEntity<?> getHotel(@RequestHeader(name = "Authorization") String token, @PathVariable Long companyId) throws Exception {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_COMPANY_OWNER") && !Objects.equals(role, "ROLE_ADMIN") && !Objects.equals(role, "ROLE_NTDA")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        CompanyPaginatedModel hotel = hotelsService.getSingleCompany(companyId);
        return new ResponseEntity<>(hotel, HttpStatus.OK);
    }

    @GetMapping("/guest-insurances")
    public ResponseEntity<?> getAllGuestInsurances(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "searchField", defaultValue = "", required = false) String searchField,
            @RequestParam(value = "companyName", defaultValue = "", required = false) String filter,
            @RequestHeader(name = "Authorization") String token
    ) {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_NTDA") && !Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_INTERPOL")  && !Objects.equals(role, "ROLE_NSA") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }

        PaginationModel guestInsurance = hotelsService.getGuestInsurances(page, pageSize, searchField, filter);
        return new ResponseEntity<>(guestInsurance, HttpStatus.OK);
    }

    @GetMapping("/reservations")
    public ResponseEntity<?> getAllReservations(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "searchField", defaultValue = "", required = false) String searchField,
            @RequestParam(value = "companyName", defaultValue = "", required = false) String filter,
            @RequestParam(value = "country", defaultValue = "", required = false) String country,
            @RequestHeader(name = "Authorization") String token
    ) {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_NTDA") && !Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_INTERPOL")  && !Objects.equals(role, "ROLE_NSA") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }

        PaginationModel reservations = hotelsService.getReservations(page, pageSize, searchField, country, filter);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/group-bookings")
    public ResponseEntity<?> getAllGroupBookings(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "searchField", defaultValue = "", required = false) String searchField,
            @RequestParam(value = "companyName", defaultValue = "", required = false) String filter,
            @RequestParam(value = "country", defaultValue = "", required = false) String country,
            @RequestHeader(name = "Authorization") String token
    ) {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_INTERPOL")  && !Objects.equals(role, "ROLE_NSA") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }

        PaginationModel groupBookings = hotelsService.getGroupBookings(page, pageSize, searchField, country, filter);
        return new ResponseEntity<>(groupBookings, HttpStatus.OK);
    }

    @GetMapping ("/reservations/details/{reservationId}")
    public ResponseEntity<?> getReservation(@RequestHeader(name = "Authorization") String token, @PathVariable Long reservationId) throws Exception {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_COMPANY_OWNER") && !Objects.equals(role, "ROLE_NTDA") && !Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_INTERPOL")  && !Objects.equals(role, "ROLE_NSA") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        ReservationDetailsDto reservation = hotelsService.getSingleReservations(reservationId);
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @GetMapping ("/guest-insurances/details/{guestInsuranceId}")
    public ResponseEntity<?> getGuestInsurance(@RequestHeader(name = "Authorization") String token, @PathVariable Long guestInsuranceId) throws Exception {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_COMPANY_OWNER") && !Objects.equals(role, "ROLE_NTDA") &&  !Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_INSURANCE_USER") && !Objects.equals(role, "ROLE_INTERPOL")  && !Objects.equals(role, "ROLE_NSA") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        GuestInsuranceDto guestInsurance = hotelsService.getSingleGuestInsurance(guestInsuranceId);
        return new ResponseEntity<>(guestInsurance, HttpStatus.OK);
    }

    @GetMapping ("/group-bookings/details/{groupBookingId}")
    public ResponseEntity<?> getGroupBooking(@RequestHeader(name = "Authorization") String token, @PathVariable Long groupBookingId) throws Exception {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_COMPANY_OWNER") && !Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_INTERPOL")  && !Objects.equals(role, "ROLE_NSA") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        GroupBookingDetailsDto groupBooking = hotelsService.getSingleGroupBooking(groupBookingId);
        return new ResponseEntity<>(groupBooking, HttpStatus.OK);
    }


    @GetMapping ("/outstanding-balance/{companyId}")
    public ResponseEntity<?> getInsuranceCompany(@RequestHeader(name = "Authorization") String token, @PathVariable Long companyId) throws Exception {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_COMPANY_OWNER") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        double outstandingBalance = hotelsService.getHotelOutstandingBalance(companyId);
        return new ResponseEntity<>(outstandingBalance, HttpStatus.OK);
    }
}