package com.terron.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KeywordOfInterestDto {

    private String keyword;
    private String reasonForInterest;
    private String remark;
    private String alertContact;
}
