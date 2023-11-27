package com.terron.repository.watchList;

import com.terron.models.watchList.PersonOfInterest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonOfInterestRepository extends JpaRepository<PersonOfInterest, Long> {

    Page<PersonOfInterest> findByFirstNameContainingOrLastNameContainingOrIdDocumentNumberContaining(String firstName, String lastName, String idDocumentNumber, Pageable pagination);

    Page<PersonOfInterest> findAllByNationality( String nationality, Pageable pagination);
}
