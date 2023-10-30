package com.terron.services.watchList;

import com.terron.dto.KeywordOfInterestDto;
import com.terron.dto.MissingPersonsDto;
import com.terron.dto.PersonOfInterestDto;
import com.terron.models.company.Company;
import com.terron.models.hotels.GuestInsurance;
import com.terron.models.watchList.KeywordOfInterest;
import com.terron.models.watchList.MissingPersons;
import com.terron.models.watchList.PersonOfInterest;
import com.terron.repository.company.CompanyRepository;
import com.terron.repository.hotels.GuestInsuranceRepository;
import com.terron.repository.watchList.KeywordOfInterestRepository;
import com.terron.repository.watchList.MissingPersonsRepository;
import com.terron.repository.watchList.PersonOfInterestRepository;
import com.terron.services.utils.DSSDashboardDto;
import com.terron.services.utils.GuestInsuranceDto;
import com.terron.services.utils.PaginationModel;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class WatchlistServiceImpl implements WatchListService{

    @Autowired
    PersonOfInterestRepository personOfInterestRepository;

            @Autowired
    MissingPersonsRepository missingPersonsRepository;

            @Autowired
    KeywordOfInterestRepository keywordOfInterestRepository;

            @Autowired
    GuestInsuranceRepository guestInsuranceRepository;

            @Autowired
    CompanyRepository companyRepository;


    @Override
    public KeywordOfInterest addKeywordOfInterest(KeywordOfInterestDto keywordOfInterestDto) throws Exception {
        KeywordOfInterest keywordOfInterest = KeywordOfInterest.builder()
                .keyword(keywordOfInterestDto.getKeyword())
                .alertContact(keywordOfInterestDto.getAlertContact())
                .reasonForInterest(keywordOfInterestDto.getReasonForInterest())
                .remark(keywordOfInterestDto.getRemark())
                .createdDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
                .build();
        keywordOfInterestRepository.save(keywordOfInterest);
        return keywordOfInterest;
    }

    @Override
    public PaginationModel getAllKeywordOfInterests(Integer page, Integer pageSize, String searchField) {
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<KeywordOfInterest> keywordOfInterests = null;
        try {
            keywordOfInterests = searchField.length() > 0
                    ? keywordOfInterestRepository.findByKeywordContaining(searchField, pagination)
                    : keywordOfInterestRepository.findAll(pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(keywordOfInterests.getTotalElements());
            paginationModel.setData(keywordOfInterests.getContent());

            return paginationModel;
        } finally {
            if (keywordOfInterests != null && keywordOfInterests instanceof Closeable) {
                try {
                    ((Closeable) keywordOfInterests).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public PersonOfInterest addPersonOfInterest(PersonOfInterestDto personOfInterestDto) throws Exception {
        PersonOfInterest personOfInterest = PersonOfInterest.builder()
                .lastName(personOfInterestDto.getLastName())
                .firstName(personOfInterestDto.getFirstName())
                .gender(personOfInterestDto.getGender())
                .dateOfBirth(personOfInterestDto.getDateOfBirth())
                .phoneNumber(personOfInterestDto.getPhoneNumber())
                .emailAddress(personOfInterestDto.getEmailAddress())
                .idDocument(personOfInterestDto.getIdDocument())
                .idDocumentNumber(personOfInterestDto.getIdDocumentNumber())
                .nationality(personOfInterestDto.getNationality())
                .height(personOfInterestDto.getHeight())
                .weight(personOfInterestDto.getWeight())
                .heightType(personOfInterestDto.getHeightType())
                .hairColor(personOfInterestDto.getDateOfBirth())
                .complexionType(personOfInterestDto.getComplexionType())
                .eyeColor(personOfInterestDto.getEyeColor())
                .reasonForInterest(personOfInterestDto.getReasonForInterest())
                .remark(personOfInterestDto.getRemark())
                .alertContact(personOfInterestDto.getAlertContact())
                .image(personOfInterestDto.getImage())
                .createdDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
                .build();
        personOfInterestRepository.save(personOfInterest);
        return personOfInterest;
    }

    @Override
    public PaginationModel getAllPersonOfInterests(Integer page, Integer pageSize, String searchField, String nationality) {
        Page<PersonOfInterest> personOfInterests = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        try {
            personOfInterests = searchField.length() > 0
                    ? personOfInterestRepository.findByFirstNameContainingOrLastNameContainingOrIdDocumentNumberContaining(searchField, searchField, searchField, pagination)
                    : nationality.length() > 0
                    ? personOfInterestRepository.findAllByNationality(nationality, pagination)
                    : personOfInterestRepository.findAll(pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(personOfInterests.getTotalElements());
            paginationModel.setData(personOfInterests.getContent());

            return paginationModel;
        } finally {
            if (personOfInterests != null && personOfInterests instanceof Closeable) {
                try {
                    ((Closeable) personOfInterests).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public MissingPersons addMissingPersons(MissingPersonsDto missingPersonsDto) throws Exception {
        MissingPersons missingPersons = MissingPersons.builder()
                .lastName(missingPersonsDto.getLastName())
                .firstName(missingPersonsDto.getFirstName())
                .idDocument(missingPersonsDto.getIdDocument())
                .image(missingPersonsDto.getImage())
                .idDocumentNumber(missingPersonsDto.getIdDocumentNumber())
                .nationality(missingPersonsDto.getNationality())
                .reasonForInterest(missingPersonsDto.getReasonForInterest())
                .remark(missingPersonsDto.getRemark())
                .alertContact(missingPersonsDto.getAlertContact())
                .createdDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
                .build();
        missingPersonsRepository.save(missingPersons);
        return missingPersons;
    }

    @Override
    public PaginationModel getAllMissingPersons(Integer page, Integer pageSize, String searchField, String nationality) {
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<MissingPersons> missingPersons = null;
        try {
            missingPersons = searchField.length() > 0
                    ? missingPersonsRepository.findByFirstNameContainingOrLastNameContainingOrIdDocumentNumberContaining(searchField, searchField, searchField, pagination)
                    : nationality.length() > 0
                    ? missingPersonsRepository.findAllByNationality(nationality, pagination)
                    : missingPersonsRepository.findAll(pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(missingPersons.getTotalElements());
            paginationModel.setData(missingPersons.getContent());

            return paginationModel;
        } finally {
            if (missingPersons != null && missingPersons instanceof Closeable) {
                try {
                    ((Closeable) missingPersons).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Object> getAllWatchLists(String filter) {
        List<MissingPersons> missingPersons = missingPersonsRepository.findAll();
        List<KeywordOfInterest> keywordOfInterests = keywordOfInterestRepository.findAll();
        List<PersonOfInterest> personOfInterests = personOfInterestRepository.findAll();

        for (MissingPersons missingPerson : missingPersons){
            missingPerson.setType("missingPersons");
        }

        for (KeywordOfInterest keywordOfInterest : keywordOfInterests){
            keywordOfInterest.setType("keywordOfInterests");
        }

        for (PersonOfInterest personOfInterest : personOfInterests){
            personOfInterest.setType("personOfInterests");
        }

        List<Object> combinedList = new ArrayList<>();
        switch (filter) {
            case "missingPersons":
                combinedList.addAll(missingPersons);
                break;
            case "keywordOfInterests":
                combinedList.addAll(keywordOfInterests);
                break;
            case "personOfInterests":
                combinedList.addAll(personOfInterests);
                break;
            default:
                combinedList.addAll(missingPersons);
                combinedList.addAll(keywordOfInterests);
                combinedList.addAll(personOfInterests);
        }
        return combinedList;
    }

    public DSSDashboardDto DSSDashboard(){
        long missingPersonsCount = missingPersonsRepository.count();
        long keywordOfInterestsCount = keywordOfInterestRepository.count();
        long personOfInterestsCount = personOfInterestRepository.count();
        List<GuestInsuranceDto> foundMissingPersons = new ArrayList<>();
        List<PersonOfInterest> personOfInterests = personOfInterestRepository.findAll();
        List<GuestInsurance> guestInsurances = guestInsuranceRepository.findAll();

        for (PersonOfInterest poi : personOfInterests) {
            for (GuestInsurance guestInsurance : guestInsurances) {
                if (poi.getEmailAddress().equals(guestInsurance.getEmailAddress()) ||
                        poi.getFirstName().equals(guestInsurance.getFirstName()) ||
                        poi.getLastName().equals(guestInsurance.getLastName()) ||
                        poi.getIdDocumentNumber().equals(guestInsurance.getIdDocumentNumber()) ||
                        poi.getPhoneNumber().equals(guestInsurance.getPhoneNumber())) {
                    Company company = companyRepository.findById(guestInsurance.getCompanyId()).get();
                    GuestInsuranceDto guestInsuranceDto = GuestInsuranceDto.builder()
                            .guestInsurance(guestInsurance)
                            .company(company)
                            .personOfInterest(poi)
                            .build();
                    foundMissingPersons.add(guestInsuranceDto);
                }
            }
        }

        return DSSDashboardDto.builder()
                .foundMissingPersons(foundMissingPersons)
                .keywordOfInterestsCount(keywordOfInterestsCount)
                .missingPersonsCount(missingPersonsCount)
                .personOfInterestsCount(personOfInterestsCount)
                .suspiciousBehaviourCount(0L)
                .build();
    }

    public PersonOfInterest getSinglePersonOfInterest(Long id) throws NotFoundException {
        return personOfInterestRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Person of interest with this id: %s does not exist", id)));
    }

}
