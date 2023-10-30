package com.terron.services.utils;

import com.terron.models.company.Company;
import com.terron.models.hotels.GuestInsurance;
import com.terron.models.watchList.PersonOfInterest;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class GuestInsuranceDto {

    private GuestInsurance guestInsurance;

    private Company company;

    private PersonOfInterest personOfInterest;
}
