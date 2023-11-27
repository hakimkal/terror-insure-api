package com.terron.repository.states;

import com.terron.models.states.LocalGovt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LgaRepository extends JpaRepository<LocalGovt, Long> {

    List<LocalGovt> findAllByStateId(Long stateId);
}
