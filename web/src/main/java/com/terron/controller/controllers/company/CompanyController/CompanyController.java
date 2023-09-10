package com.terron.controller.controllers.company.CompanyController;

import com.terron.dto.OnboardCompanyDto;
import com.terron.services.company.CompanyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public class CompanyController {

    @Autowired
    CompanyServiceImpl companyService;

    @PostMapping("/")
    public ResponseEntity<?> onboardCompany(@RequestBody OnboardCompanyDto onboardCompanyDto) throws Exception {
        companyService.onboardCompany(onboardCompanyDto);
        return new ResponseEntity<>("Company onboarding successful.", HttpStatus.OK);
    }
}
