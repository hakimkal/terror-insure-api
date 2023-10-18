package com.terron.services.utils;

import com.terron.models.company.Company;
import com.terron.models.hotels.Reservations;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationDetailsDto {

    private Reservations reservation;

    private Company company;
}
