package com.terron.repository.payment;

import com.terron.models.payment.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByCompanyId(Long companyId);

    List<Payment> findAllByInsuranceCompany(String insuranceCompany);

    Page<Payment> findAllByDatePaidBetween(String startDate, String endDate, Pageable pagination);

    Page<Payment> findAllByCompanyIdAndDatePaidBetween(Long id, String startDate, String endDate, Pageable pageable);

    Page<Payment> findAllByInsuranceCompany(String insuranceCompany, Pageable pageable);

    Page<Payment> findAllByInsuranceCompanyAndDatePaidBetween(String insuranceCompany, String startDate, String endDate, Pageable pageable);

    Page<Payment> findAllByCompanyId(Long companyId, Pageable pageable);
}
