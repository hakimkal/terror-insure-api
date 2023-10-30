package com.terron.controller.controllers.alerts;

import com.terron.dto.AlertContactDto;
import com.terron.dto.OrganizationDto;
import com.terron.models.alerts.AlertContacts;
import com.terron.models.alerts.Organizations;
import com.terron.models.watchList.PersonOfInterest;
import com.terron.response.ResponseDetails;
import com.terron.response.ResponseDetailsWithObject;
import com.terron.services.alerts.AlertServiceImpl;
import com.terron.services.hotels.HotelsServiceImpl;
import com.terron.services.utils.CompanyPaginatedModel;
import com.terron.services.utils.DSSDashboardDto;
import com.terron.services.utils.PaginationModel;
import com.terron.services.watchList.WatchlistServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.terron.utils.utility.decodeToken;

@RestController
@RequestMapping("/v1")
public class GeneralController {

    @Autowired
    AlertServiceImpl alertService;

    @Autowired
    HotelsServiceImpl hotelsService;

    @Autowired
    WatchlistServiceImpl watchlistService;

    @PostMapping("/dss/organizations")
    public ResponseEntity<?> addOrganization(@Valid @RequestBody OrganizationDto organizationDto, @RequestHeader(name = "Authorization") String token) throws Exception {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        Organizations organizations = alertService.addOrganizations(organizationDto);
        ResponseDetailsWithObject responseDetails = new ResponseDetailsWithObject(LocalDateTime.now(), "Organization added successfully", organizations, "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }

    @GetMapping("/dss/organizations")
    public ResponseEntity<?> getOrganizations(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "searchField", defaultValue = "", required = false) String searchField,
            @RequestParam(value = "organizationType", defaultValue = "", required = false) String organizationType,
            @RequestHeader(name = "Authorization") String token
    ) {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        PaginationModel organizations = alertService.getOrganizations(page, pageSize, searchField, organizationType);
        return new ResponseEntity<>(organizations, HttpStatus.OK);
    }

    @PostMapping("/dss/alert-contact")
    public ResponseEntity<?> addAlertContact(@Valid @RequestBody AlertContactDto alertContactDto, @RequestHeader(name = "Authorization") String token) throws Exception {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        AlertContacts alertContacts = alertService.addAlertContact(alertContactDto);
        ResponseDetailsWithObject responseDetails = new ResponseDetailsWithObject(LocalDateTime.now(), "Alert contact added successfully", alertContacts, "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }

    @GetMapping("/dss/alert-contacts")
    public ResponseEntity<?> getAlertContacts(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "searchField", defaultValue = "", required = false) String searchField,
            @RequestParam(value = "organization", defaultValue = "", required = false) String organization,
            @RequestHeader(name = "Authorization") String token
    ) {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        PaginationModel alertContacts = alertService.getAllAlertContacts(page, pageSize, searchField, organization);
        return new ResponseEntity<>(alertContacts, HttpStatus.OK);
    }

    @GetMapping("/dss/guest-insurances")
    public ResponseEntity<?> getAllGuestInsurances(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "searchField", defaultValue = "", required = false) String searchField,
            @RequestParam(value = "companyName", defaultValue = "", required = false) String filter,
            @RequestHeader(name = "Authorization") String token
    ) {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }

        PaginationModel guestInsurance = hotelsService.getGuestInsurances(page, pageSize, searchField, filter);
        return new ResponseEntity<>(guestInsurance, HttpStatus.OK);
    }

    @GetMapping("/dss/reservations")
    public ResponseEntity<?> getAllReservations(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "searchField", defaultValue = "", required = false) String searchField,
            @RequestParam(value = "companyName", defaultValue = "", required = false) String filter,
            @RequestParam(value = "country", defaultValue = "", required = false) String country,
            @RequestHeader(name = "Authorization") String token
    ) {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }

        PaginationModel reservations = hotelsService.getReservations(page, pageSize, searchField, country, filter);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }


    @GetMapping("/dss/watch-list")
    public ResponseEntity<?> getAllWatchList(
            @RequestParam(value = "filter", defaultValue = "", required = false) String filter,
            @RequestHeader(name = "Authorization") String token
    ) {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_ADMIN") && !Objects.equals(role, "ROLE_NTDC")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        List<Object> allWatchLists = watchlistService.getAllWatchLists(filter);
        return new ResponseEntity<>(allWatchLists, HttpStatus.OK);
    }

    @GetMapping("/dss/dashboard")
    public ResponseEntity<?> dssDashboard(
            @RequestHeader(name = "Authorization") String token
    ) {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        DSSDashboardDto dssDashboardDto = watchlistService.DSSDashboard();
        return new ResponseEntity<>(dssDashboardDto, HttpStatus.OK);
    }

    @GetMapping ("/dss/watchlist/{id}")
    public ResponseEntity<?> getSingleWatchlist(@RequestHeader(name = "Authorization") String token, @PathVariable Long id) throws Exception {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        PersonOfInterest personOfInterest = watchlistService.getSinglePersonOfInterest(id);
        return new ResponseEntity<>(personOfInterest, HttpStatus.OK);
    }
}
