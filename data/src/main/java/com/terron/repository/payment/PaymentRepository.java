package com.terron.repository.payment;

import com.terron.models.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByCompanyId(Long companyId);

    List<Payment> findAllByInsuranceCompany(String insuranceCompany);
}
