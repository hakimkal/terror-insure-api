package com.terron.controller.controllers.company.CompanyController;

import com.terron.dto.OnboardCompanyDto;
import com.terron.models.company.Company;
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

    @PostMapping("/onboard/{userId}")
    public ResponseEntity<?> onboardCompany(@PathVariable Long userId, @Valid @RequestBody OnboardCompanyDto onboardCompanyDto) throws Exception {
        Company company = companyService.onboardCompany(userId,onboardCompanyDto);
        ResponseDetailsWithObject responseDetails = new ResponseDetailsWithObject(LocalDateTime.now(), "Company onboarding successful.",company, "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }
}
