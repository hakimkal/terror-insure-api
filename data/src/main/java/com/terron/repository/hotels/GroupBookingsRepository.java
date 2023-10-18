package com.terron.repository.hotels;

import com.terron.models.hotels.GroupBookings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupBookingsRepository extends JpaRepository<GroupBookings, Long> {

    Page<GroupBookings> findAllByCompanyIdAndFirstNameContainingOrLastNameContainingOrReservationNumberContaining(Long companyId, String firstName, String lastName, String reservationNumber, Pageable pagination);

    Page<GroupBookings> findAllByCompanyIdAndCountryOfDeparture(Long companyId, String country, Pageable pagination);

    Page<GroupBookings> findAllByCompanyId(Long companyId, Pageable pagination);

    Long countAllByCompanyId(Long companyId);

    Page<GroupBookings> findAllByFirstNameContainingOrLastNameContainingOrReservationNumberContaining(String firstName, String lastName, String reservationNumber, Pageable pagination);

    Page<GroupBookings> findAllByCountryOfDeparture( String country, Pageable pagination);
}
