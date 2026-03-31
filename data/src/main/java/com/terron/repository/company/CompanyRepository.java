package com.terron.repository.company;

import com.terron.models.company.Company;
import com.terron.models.company.CompanyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Boolean existsByOfficialEmailAddress(String email);

    Boolean existsByCacNumber(String cacNumber);

    Page<Company> findAllByCompanyTypeAndCompanyNameContainingOrCacNumberContaining(CompanyType companyType,String name, String cacNumber, Pageable pagination);

    Page<Company> findAllByCompanyTypeAndState(CompanyType companyType,String state, Pageable pagination);

    Long countAllByCompanyType(CompanyType companyType);

    Page<Company> findAllByCompanyType(CompanyType companyType, Pageable pagination);

    List<Company> findAllByCompanyType(CompanyType companyType);

    Company findByCompanyName(String companyName);

    Company findByOfficialEmailAddress(String officialEmailAddress);

    Long countAllByInsuranceCompanyId(Long insuranceCompany);

//    List<Company> getCompaniesByInsuranceCompanyId(String insuranceCompany);
}
