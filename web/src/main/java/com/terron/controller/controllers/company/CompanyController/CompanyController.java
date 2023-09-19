package com.terron.controller.controllers.company.CompanyController;

import com.terron.dto.OnboardCompanyDto;
import com.terron.models.company.Company;
import com.terron.models.user.Users;
import com.terron.repository.user.UserRepository;
import com.terron.response.ResponseDetailsWithObject;
import com.terron.services.company.CompanyServiceImpl;
import com.terron.services.utils.PaginationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/company")
public class CompanyController {

    @Autowired
    CompanyServiceImpl companyService;

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
    public ResponseEntity<?> addHotel(@Valid @RequestBody OnboardCompanyDto onboardCompanyDto) throws Exception {
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
            @RequestParam(value = "state", defaultValue = "", required = false) String state
    ) {
        PaginationModel company = companyService.getAllHotels(page, pageSize, searchField, state);
        return new ResponseEntity<>(company, HttpStatus.OK);
    }

}
