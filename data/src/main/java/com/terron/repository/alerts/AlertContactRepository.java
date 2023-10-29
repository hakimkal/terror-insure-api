package com.terron.repository.alerts;

import com.terron.models.alerts.AlertContacts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertContactRepository extends JpaRepository<AlertContacts, Long> {

    Page<AlertContacts> findByLastNameContainingOrFirstNameContaining(String lastName, String firstName, Pageable pagination);

    Page<AlertContacts> findAllByOrganization(String organization, Pageable pagination);
}
