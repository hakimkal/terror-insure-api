package com.terron.services.company;

import com.terron.dto.OnboardCompanyDto;
import com.terron.models.company.Company;

public interface CompanyService {
    Company onboardCompany(OnboardCompanyDto onboardCompanyDto) throws Exception;

    Company addHotel(OnboardCompanyDto onboardCompanyDto) throws Exception;
}
