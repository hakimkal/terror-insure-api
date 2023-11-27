package com.terron.services.utils;

import lombok.Data;

import java.util.List;

@Data
public class PaginationModel {

    private long totalCount;
    private List<?> data;
}
