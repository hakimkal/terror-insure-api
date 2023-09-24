package com.terron.repository.hotels;

import com.terron.models.hotels.Reservations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationsRepository extends JpaRepository<Reservations, Long> {

    Page<Reservations> findByCompanyIdAndFirstNameContainingOrLastNameContainingOrReservationNumberContaining(Long companyId, String firstName, String lastName, String reservationNumber, Pageable pagination);

    Page<Reservations> findAllByCompanyIdAndCountryOfDeparture(Long companyId, String country, Pageable pagination);

    Page<Reservations> findAllByCompanyId(Long companyId, Pageable pagination);
}
