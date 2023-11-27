package com.terron.controller.controllers;

import com.terron.models.states.LocalGovt;
import com.terron.models.states.State;
import com.terron.repository.states.LgaRepository;
import com.terron.repository.states.StatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/nigeria")
public class NigerianStateController {

    @Autowired
    private StatesRepository statesRepository;

    @Autowired
    private LgaRepository lgaRepository;

    @GetMapping("/states")
    public List<State> getStates() {
        return statesRepository.findAll();
    }

    @GetMapping("/state/{stateId}/lga")
    public List<LocalGovt> getStateLga(@PathVariable Long stateId) {
        return lgaRepository.findAllByStateId(stateId);
    }
}
