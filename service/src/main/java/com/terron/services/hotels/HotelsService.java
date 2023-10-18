package com.terron.services.hotels;

import com.terron.dto.CreateGuestInsuranceDto;
import com.terron.dto.CreateReservationDto;
import com.terron.exceptions.UserAlreadyExistException;
import com.terron.models.hotels.GuestInsurance;
import com.terron.services.utils.CompanyPaginatedModel;
import com.terron.services.utils.PaginationModel;

public interface HotelsService {

    void createReservation(CreateReservationDto createReservationDto, Long companyId) throws UserAlreadyExistException;

    GuestInsurance createGuestInsurance(CreateGuestInsuranceDto createGuestInsuranceDto, Long companyId) throws UserAlreadyExistException;

    PaginationModel getAllReservations(Integer page, Integer pageSize, String searchField, String country, Long companyId);

    PaginationModel getAllGuestInsurance(Integer page, Integer pageSize, String searchField, Long companyId);

    PaginationModel getAllUsers(Integer page, Integer pageSize, String searchField, Long companyId);

    CompanyPaginatedModel getSingleCompany(Long companyId) throws UserAlreadyExistException;
}
