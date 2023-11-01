package com.terron.repository.payment;

import com.terron.models.payment.DailyTransactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyTransactionRepository extends JpaRepository<DailyTransactions, Long> {

    List<DailyTransactions> findAllByCompanyId(Long companyId);

    List<DailyTransactions> findAllByInsuranceCompany(String insuranceCompany);

    Page<DailyTransactions> findAllByTransactionDateBetween(String startDate, String endDate, Pageable pagination);

    Page<DailyTransactions> findAllByCompanyIdAndTransactionDateBetween(Long id, String startDate, String endDate, Pageable pageable);

    Page<DailyTransactions> findAllByInsuranceCompany(String insuranceCompany, Pageable pageable);

    Page<DailyTransactions> findAllByInsuranceCompanyAndTransactionDateBetween(String insuranceCompany, String startDate, String endDate, Pageable pageable);

    Page<DailyTransactions> findAllByCompanyId(Long companyId, Pageable pageable);
}
