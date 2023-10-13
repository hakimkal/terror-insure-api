package com.terron.services.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestInsuranceResponseDto {

    private long companyCount;
    private long usersCount;
    private long insuredVolume;
    private float ntdcUsers;
    private float hotelUser;
    private float insuranceUsers;
    private double totalAmountInsured;
    private double outstandingAmount;
    private double amountPaid;
}
