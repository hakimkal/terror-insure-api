package com.terron.repository.alerts;

import com.terron.models.alerts.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Page<Organization> findByOrganizationNameContainingOrOrganizationTypeContaining(String organizationName, String organizationType, Pageable pagination);

    Page<Organization> findAllByOrganizationType( String organizationType, Pageable pagination);
}
