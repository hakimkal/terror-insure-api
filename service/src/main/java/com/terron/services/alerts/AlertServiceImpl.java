package com.terron.services.alerts;

import com.terron.dto.AlertContactDto;
import com.terron.dto.OrganizationDto;
import com.terron.models.alerts.AlertContacts;
import com.terron.models.alerts.Organizations;
import com.terron.repository.alerts.AlertContactRepository;
import com.terron.repository.alerts.OrganizationRepository;
import com.terron.services.utils.PaginationModel;
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

@Service
@Slf4j
public class AlertServiceImpl implements AlertService{

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    AlertContactRepository alertContactRepository;

    @Override
    public AlertContacts addAlertContact(AlertContactDto alertContactDto) {

        AlertContacts alertContacts = AlertContacts.builder()
                .lastName(alertContactDto.getLastName())
                .firstName(alertContactDto.getFirstName())
                .email(alertContactDto.getEmail())
                .phoneNumber(alertContactDto.getPhoneNumber())
                .jobTitle(alertContactDto.getJobTitle())
                .organization(alertContactDto.getOrganization())
                .createdDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
                .build();

        alertContactRepository.save(alertContacts);
        return alertContacts;
    }

    @Override
    public PaginationModel getAllAlertContacts(Integer page, Integer pageSize, String searchField, String organization) {
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<AlertContacts> alertContacts = null;
        try {
            alertContacts = searchField.length() > 0
                    ? alertContactRepository.findByLastNameContainingOrFirstNameContaining(searchField, searchField, pagination)
                    : organization.length() > 0
                    ? alertContactRepository.findAllByOrganization(organization, pagination)
                    : alertContactRepository.findAll(pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(alertContacts.getTotalElements());
            paginationModel.setData(alertContacts.getContent());

            return paginationModel;
        } finally {
            if (alertContacts != null && alertContacts instanceof Closeable) {
                try {
                    ((Closeable) alertContacts).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Organizations addOrganizations(OrganizationDto organizationDto) {
        Organizations organizations = Organizations.builder()
                .organizationName(organizationDto.getOrganizationName())
                .organizationType(organizationDto.getOrganizationType())
                .email(organizationDto.getEmail())
                .phoneNumber(organizationDto.getPhoneNumber())
                .address(organizationDto.getAddress())
                .abbreviation(organizationDto.getAbbreviation())
                .state(organizationDto.getState())
                .logo(organizationDto.getLogo())
                .createdDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
                .build();
        organizationRepository.save(organizations);
        return organizations;
    }

    @Override
    public PaginationModel getOrganizations(Integer page, Integer pageSize, String searchField, String organizationType) {
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Organizations> organizations = null;
        try {
            organizations = searchField.length() > 0
                    ? organizationRepository.findByOrganizationNameContainingOrOrganizationTypeContaining(searchField, searchField, pagination)
                    : organizationType.length() > 0
                    ? organizationRepository.findAllByOrganizationType(organizationType, pagination)
                    : organizationRepository.findAll(pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(organizations.getTotalElements());
            paginationModel.setData(organizations.getContent());

            return paginationModel;
        } finally {
            if (organizations != null && organizations instanceof Closeable) {
                try {
                    ((Closeable) organizations).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
