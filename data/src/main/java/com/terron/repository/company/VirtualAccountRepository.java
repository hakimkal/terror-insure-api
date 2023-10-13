package com.terron.repository.company;

import com.terron.models.company.VirtualAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VirtualAccountRepository extends JpaRepository<VirtualAccount, Long> {

    VirtualAccount findByCompanyId(Long companyId);
}
