package com.terron.controller.controllers.watchList;

import com.terron.dto.KeywordOfInterestDto;
import com.terron.dto.MissingPersonsDto;
import com.terron.dto.PersonOfInterestDto;
import com.terron.models.watchList.KeywordOfInterest;
import com.terron.models.watchList.MissingPersons;
import com.terron.models.watchList.PersonOfInterest;
import com.terron.repository.user.UserRepository;
import com.terron.response.ResponseDetails;
import com.terron.response.ResponseDetailsWithObject;
import com.terron.services.utils.PaginationModel;
import com.terron.services.watchList.WatchlistServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.terron.utils.utility.decodeToken;

@RestController
@RequestMapping("/v1/watch-list")
public class WatchListController {

    @Autowired
    WatchlistServiceImpl watchlistService;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/keyword-of-interests")
    public ResponseEntity<?> addKeywordOfInterest(@Valid @RequestBody KeywordOfInterestDto keywordOfInterestDto, @RequestHeader(name = "Authorization") String token) throws Exception {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        KeywordOfInterest keywordOfInterest = watchlistService.addKeywordOfInterest(keywordOfInterestDto);
        ResponseDetailsWithObject responseDetails = new ResponseDetailsWithObject(LocalDateTime.now(), "Keyword of interest added successfully", keywordOfInterest, "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }

    @GetMapping("/keyword-of-interests")
    public ResponseEntity<?> getKeywordOfInterests(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "searchField", defaultValue = "", required = false) String searchField,
            @RequestHeader(name = "Authorization") String token
    ) {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        PaginationModel reservations = watchlistService.getAllKeywordOfInterests(page, pageSize, searchField);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @PostMapping("/person-of-interests")
    public ResponseEntity<?> addPersonOfInterest(@Valid @RequestBody PersonOfInterestDto personOfInterestDto, @RequestHeader(name = "Authorization") String token) throws Exception {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        PersonOfInterest personOfInterest = watchlistService.addPersonOfInterest(personOfInterestDto);
        ResponseDetailsWithObject responseDetails = new ResponseDetailsWithObject(LocalDateTime.now(), "Person of interest added successfully", personOfInterest, "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }

    @GetMapping("/person-of-interests")
    public ResponseEntity<?> getPersonOfInterests(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "searchField", defaultValue = "", required = false) String searchField,
            @RequestParam(value = "nationality", defaultValue = "", required = false) String nationality,
            @RequestHeader(name = "Authorization") String token
    ) {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        PaginationModel personOfInterests = watchlistService.getAllPersonOfInterests(page, pageSize, searchField, nationality);
        return new ResponseEntity<>(personOfInterests, HttpStatus.OK);
    }

    @PostMapping("/missing-persons")
    public ResponseEntity<?> addMissingPersons(@Valid @RequestBody MissingPersonsDto missingPersonsDto, @RequestHeader(name = "Authorization") String token) throws Exception {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        MissingPersons missingPersons = watchlistService.addMissingPersons(missingPersonsDto);
        ResponseDetailsWithObject responseDetails = new ResponseDetailsWithObject(LocalDateTime.now(), "Missing persons added successfully", missingPersons, "success");
        return new ResponseEntity<>(responseDetails, HttpStatus.OK);
    }

    @GetMapping("/missing-persons")
    public ResponseEntity<?> getMissingPeople(
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize,
            @RequestParam(value = "searchField", defaultValue = "", required = false) String searchField,
            @RequestParam(value = "nationality", defaultValue = "", required = false) String nationality,
            @RequestHeader(name = "Authorization") String token
    ) {
        String role = decodeToken(token);
        if (!Objects.equals(role, "ROLE_DSS") && !Objects.equals(role, "ROLE_ADMIN")) {
            ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), "Access is denied", "error");
            return new ResponseEntity<>(responseDetails, HttpStatus.FORBIDDEN);
        }
        PaginationModel allMissingPersons = watchlistService.getAllMissingPersons(page, pageSize, searchField, nationality);
        return new ResponseEntity<>(allMissingPersons, HttpStatus.OK);
    }
}
