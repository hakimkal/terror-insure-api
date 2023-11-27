package com.terron.services.utils;

import com.terron.models.company.Company;
import com.terron.models.hotels.GroupBookings;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupBookingDetailsDto {

    private GroupBookings groupBooking;

    private Company company;
}
