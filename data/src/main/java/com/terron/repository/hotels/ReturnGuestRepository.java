package com.terron.repository.hotels;
import com.terron.models.hotels.ReturnGuests;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReturnGuestRepository extends JpaRepository<ReturnGuests, Long> {

    Page<ReturnGuests> findAllByCompanyIdAndFirstNameContainingOrLastNameContainingOrReservationNumberContaining(Long companyId, String firstName, String lastName, String reservationNumber, Pageable pagination);

    Page<ReturnGuests> findAllByCompanyIdAndCountryOfDeparture(Long companyId, String country, Pageable pagination);

    Page<ReturnGuests> findAllByCompanyId(Long companyId, Pageable pagination);
}
