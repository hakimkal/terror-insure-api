package com.terron.services.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginationModel {
    private long totalCount;
    private List<?> data;
    private long totalHotels;
    private long totalUsers;
}
