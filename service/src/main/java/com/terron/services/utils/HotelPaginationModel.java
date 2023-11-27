package com.terron.services.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HotelPaginationModel {
    private long totalCount;
    private List<?> data;
    private long totalHotels;
    private long totalGuest;
    private long totalReservations;
    private long newGuests;
    private long returnGuest;
}
