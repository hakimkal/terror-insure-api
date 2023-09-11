package com.terron.services.company;

import com.terron.dto.OnboardCompanyDto;
import com.terron.models.company.Company;

public interface CompanyService {
    Company onboardCompany(Long userId, OnboardCompanyDto onboardCompanyDto) throws Exception;
}
