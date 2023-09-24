package com.terron.services.hotels;

import com.terron.dto.CreateGuestInsuranceDto;
import com.terron.dto.CreateReservationDto;
import com.terron.exceptions.UserAlreadyExistException;
import com.terron.models.hotels.GuestInsurance;
import com.terron.models.hotels.Reservations;
import com.terron.services.utils.PaginationModel;

public interface HotelsService {

    Reservations createReservation(CreateReservationDto createReservationDto, Long companyId) throws UserAlreadyExistException;

    GuestInsurance createGuestInsurance(CreateGuestInsuranceDto createGuestInsuranceDto, Long companyId) throws UserAlreadyExistException;

    PaginationModel getAllReservations(Integer page, Integer pageSize, String searchField, String country, Long companyId);

    PaginationModel getAllGuestInsurance(Integer page, Integer pageSize, String searchField, Long companyId);
}
