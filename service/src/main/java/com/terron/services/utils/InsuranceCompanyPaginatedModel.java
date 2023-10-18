package com.terron.services.utils;

import lombok.Data;

import java.util.List;

@Data
public class InsuranceCompanyPaginatedModel {
    private long totalCount;
    private List<?> data;
    private long totalInsuranceCompanies;
    private double totalAmountInsured;
    private double outstandingAmount;
    private double amountPaid;
}
