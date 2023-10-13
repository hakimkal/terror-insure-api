package com.terron.services.utils;

import com.terron.models.company.Company;
import com.terron.models.user.Users;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailsDto {

    private Users user;

    private Company company;
}
