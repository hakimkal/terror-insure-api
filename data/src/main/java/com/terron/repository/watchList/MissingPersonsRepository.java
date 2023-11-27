package com.terron.repository.watchList;

import com.terron.models.watchList.MissingPersons;
import com.terron.models.watchList.PersonOfInterest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissingPersonsRepository extends JpaRepository<MissingPersons, Long> {

    Page<MissingPersons> findByFirstNameContainingOrLastNameContainingOrIdDocumentNumberContaining(String firstName, String lastName, String idDocumentNumber, Pageable pagination);

    Page<MissingPersons> findAllByNationality( String nationality, Pageable pagination);
}
