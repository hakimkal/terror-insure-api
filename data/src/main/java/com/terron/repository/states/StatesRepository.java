package com.terron.repository.states;

import com.terron.models.states.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatesRepository extends JpaRepository<State, Long> {
}
