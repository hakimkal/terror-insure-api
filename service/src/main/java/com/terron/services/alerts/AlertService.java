package com.terron.services.alerts;

import com.terron.dto.*;
import com.terron.models.alerts.AlertContacts;
import com.terron.models.alerts.Organizations;
import com.terron.services.utils.PaginationModel;

public interface AlertService {

    AlertContacts addAlertContact(AlertContactDto alertContactDto) throws Exception;

    PaginationModel getAllAlertContacts(Integer page, Integer pageSize, String searchField, String organization);

    Organizations addOrganizations(OrganizationDto organizationDto) throws Exception;

    PaginationModel getOrganizations(Integer page, Integer pageSize, String searchField, String OrganizationType);
}
