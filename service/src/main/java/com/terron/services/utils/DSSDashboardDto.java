package com.terron.services.utils;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class DSSDashboardDto {
    private List<?> foundMissingPersons;
    private long missingPersonsCount;
    private long keywordOfInterestsCount;
    private long personOfInterestsCount;
    private long suspiciousBehaviourCount;
}
