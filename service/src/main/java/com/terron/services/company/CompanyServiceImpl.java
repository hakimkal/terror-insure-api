package com.terron.services.company;

import com.terron.dto.OnboardCompanyDto;
import com.terron.exceptions.UserAlreadyExistException;
import com.terron.models.company.Company;
import com.terron.models.user.UserRole;
import com.terron.models.user.Users;
import com.terron.repository.company.CompanyRepository;
import com.terron.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.Component;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService{

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    @Autowired
    ModelMapper modelMapper;


    @Override
    public void onboardCompany(OnboardCompanyDto onboardCompanyDto) throws Exception {
        Company company;
        boolean userExists = userRepository
                .existsById(onboardCompanyDto.getUserId());

        if (!userExists) {
            throw new UserAlreadyExistException(String.format("User with id: %s does not exists", onboardCompanyDto.getUserId()));
        }

        company = modelMapper.map(onboardCompanyDto, Company.class);
        companyRepository.save(company);
        Users user = userRepository.findById(onboardCompanyDto.getUserId()).get();
        user.setCompany(company);
        userRepository.save(user);
    }
}
