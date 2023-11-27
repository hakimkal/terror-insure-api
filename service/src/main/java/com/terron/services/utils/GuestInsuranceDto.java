package com.terron.services.utils;

import com.terron.models.company.Company;
import com.terron.models.hotels.GuestInsurance;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GuestInsuranceDto {

    private GuestInsurance guestInsurance;

    private Company company;
}
