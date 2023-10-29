package com.terron.repository.alerts;

import com.terron.models.alerts.Organizations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organizations, Long> {

    Page<Organizations> findByOrganizationNameContainingOrOrganizationTypeContaining(String organizationName, String organizationType, Pageable pagination);

    Page<Organizations> findAllByOrganizationType(String organizationType, Pageable pagination);
}
