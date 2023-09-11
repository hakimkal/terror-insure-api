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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

@Slf4j
@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService{

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    @Autowired
    ModelMapper modelMapper;


    @Override
    @Transactional
    public Company onboardCompany(Long userId,OnboardCompanyDto onboardCompanyDto) throws UserAlreadyExistException {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserAlreadyExistException("User with id: " + userId + " does not exist"));
        Company company = modelMapper.map(onboardCompanyDto, Company.class);
        company.addUser(user);
        return companyRepository.save(company);
    }

}
