package com.terron.services.alerts;

import com.terron.dto.*;
import com.terron.models.alerts.AlertContact;
import com.terron.models.alerts.Organization;
import com.terron.services.utils.PaginationModel;

public interface AlertService {

    AlertContact addAlertContact(AlertContactDto alertContactDto) throws Exception;

    PaginationModel getAllAlertContacts(Integer page, Integer pageSize, String searchField, String organization);

    Organization addOrganizations(OrganizationDto organizationDto) throws Exception;

    PaginationModel getOrganizations(Integer page, Integer pageSize, String searchField, String OrganizationType);
}
