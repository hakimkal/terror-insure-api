package com.terron.repository.alerts;

import com.terron.models.alerts.AlertContact;
import com.terron.models.alerts.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertContactRepository extends JpaRepository<AlertContact, Long> {

    Page<AlertContact> findByLastNameContainingOrFirstNameContaining(String lastName, String firstName, Pageable pagination);

    Page<AlertContact> findAllByOrganization( String organization, Pageable pagination);
}
