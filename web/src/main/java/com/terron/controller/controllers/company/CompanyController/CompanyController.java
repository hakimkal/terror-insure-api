package com.terron.controller.controllers.company.CompanyController;

import com.terron.dto.OnboardCompanyDto;
import com.terron.models.company.Company;
import com.terron.models.user.Users;
import com.terron.repository.user.UserRepository;
import com.terron.response.ResponseDetailsWithObject;
import com.terron.services.company.CompanyServiceImpl;
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
}
