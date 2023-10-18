package com.terron.controller.controllers.company;

import com.terron.dto.OnboardCompanyDto;
import com.terron.models.company.Company;
import com.terron.models.company.VirtualAccount;
import com.terron.models.user.Users;
import com.terron.repository.user.UserRepository;
import com.terron.response.ResponseDetails;
import com.terron.response.ResponseDetailsWithObject;
import com.terron.services.company.CompanyServiceImpl;
import com.terron.services.hotels.HotelsServiceImpl;
import com.terron.services.utils.*;
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
@RequestMapping("/v1/company")
public class CompanyController {

    @Autowired
    CompanyServiceImpl companyService;

    @Autowired
    HotelsServiceImpl hotelsService;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/onboard")
    public ResponseEntity<?> onboardCompany(@Valid @RequestBody OnboardCompanyDto onboardCompanyDto) throws Exception {
        Company company = companyService.onboardCompany(onboardCompanyDto);
        Users user = userRepository.findByEmailAddress(onboardCompanyDto.getOfficialEmailAddress()).get();
        ResponseDetailsWithObject responseDetails = new ResponseDetailsWithObject(LocalDateTime.now(), String.format("Company onboarding successful, A verification code: %s has been sent to you", user.getVerificationToken()), company, "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }

    @PostMapping("/hotel")
    public ResponseEntity<?> addHotel(@Valid @RequestBody OnboardCompanyDto onboardCompanyDto, @RequestHeader(name = "Authorization") String token) throws Exception {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }

        Company company = companyService.addHotel(onboardCompanyDto);
        Users user = userRepository.findByEmailAddress(onboardCompanyDto.getOfficialEmailAddress()).get();
        ResponseDetailsWithObject responseDetails = new ResponseDetailsWithObject(LocalDateTime.now(), String.format("Hotel added successfully, A verification code: %s has been sent to contact person email address", user.getVerificationToken()), company, "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }

    @GetMapping("/hotels")
    public ResponseEntity<?> getHotels(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "searchField", defaultValue = "", required = false) String searchField,
            @RequestParam(value = "state", defaultValue = "", required = false) String state,
            @RequestHeader(name = "Authorization") String token
    ) {
        String role = decodeToken(token);
        if(!Objects.equals(role, "ROLE_ADMIN") && !Objects.equals(role, "ROLE_NTDC")){
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        HotelPaginationModel company = companyService.getAllHotels(page, pageSize, searchField, state);
        return new ResponseEntity<>(company, HttpStatus.OK);
    }

    @GetMapping("/insurance-companies")
    public ResponseEntity<?> getInsuranceCompanies(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "searchField", defaultValue = "", required = false) String searchField,
            @RequestParam(value = "state", defaultValue = "", required = false) String state,
            @RequestHeader(name = "Authorization") String token
    ) {
        String role = decodeToken(token);
        if(!Objects.equals(role, "ROLE_ADMIN") && !Objects.equals(role, "ROLE_NTDC")){
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        InsuranceCompanyPaginatedModel insuranceCompanyPaginatedModel = companyService.getAllInsuranceCompany(page, pageSize, searchField, state);
        return new ResponseEntity<>(insuranceCompanyPaginatedModel, HttpStatus.OK);
    }

    @GetMapping("/all-insurance-companies")
    public ResponseEntity<?> getInsuranceCompanies() {
        List<Company> companies = companyService.getAllInsuranceCompanies();
        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    @GetMapping ("/details/{companyId}")
    public ResponseEntity<?> getInsuranceCompany(@RequestHeader(name = "Authorization") String token, @PathVariable Long companyId) throws Exception {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_INSURANCE_USER") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        CompanyPaginatedModel hotel = hotelsService.getSingleCompany(companyId);
        return new ResponseEntity<>(hotel, HttpStatus.OK);
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
        if (!Objects.equals(role, "ROLE_INSURANCE_USER") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }

        PaginationModel users = hotelsService.getAllUsers(page, pageSize, searchField, companyId);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping ("/{companyId}/account-details")
    public ResponseEntity<?> getCompanyAccountDetails(@RequestHeader(name = "Authorization") String token, @PathVariable Long companyId) throws Exception {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_INSURANCE_USER") && !Objects.equals(role, "ROLE_COMPANY_OWNER") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        VirtualAccount virtualAccount = companyService.getCompanyAccountDetails(companyId);
        return new ResponseEntity<>(virtualAccount, HttpStatus.OK);
    }

    @GetMapping ("/insurance/general-report/{companyId}")
    public ResponseEntity<?> insuranceCompanyGeneralReport(@RequestHeader(name = "Authorization") String token, @PathVariable Long companyId) throws Exception {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_INSURANCE_USER") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        GuestInsuranceResponseDto guestInsuranceResponseDto = companyService.insuranceCompanyGeneralReport(companyId);
        return new ResponseEntity<>(guestInsuranceResponseDto, HttpStatus.OK);
    }
}
