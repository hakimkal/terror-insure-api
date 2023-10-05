package com.terron.services.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompanyPaginatedModel {

    private long newGuest;
    private float newGuestsPercentage;
    private Object data;
    private long totalReservations;
    private float totalReservationsPercentage;
    private long totalGuest;
    private float totalGuestsPercentage;
    private double totalAmountInsured;
    private double outstandingAmount;
    private double amountPaid;
}
