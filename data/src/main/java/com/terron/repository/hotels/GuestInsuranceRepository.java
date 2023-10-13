package com.terron.repository.hotels;

import com.terron.models.hotels.GuestInsurance;
import com.terron.models.hotels.Reservations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestInsuranceRepository extends JpaRepository<GuestInsurance, Long> {
    Page<GuestInsurance> findByCompanyIdAndFirstNameContainingOrLastNameContainingOrCertificateNumberContaining(Long companyId,String firstName, String lastName, String certificateNumber, Pageable pagination);

    Page<GuestInsurance> findAllByCompanyId(Long companyId, Pageable pagination);

    Long countAllByCompanyId(Long companyId);

    Page<GuestInsurance> findByFirstNameContainingOrLastNameContainingOrCertificateNumberContaining(String firstName, String lastName, String certificateNumber, Pageable pagination);

}
