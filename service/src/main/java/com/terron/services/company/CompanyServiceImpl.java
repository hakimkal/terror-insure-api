package com.terron.services.company;

import com.terron.dto.OnboardCompanyDto;
import com.terron.exceptions.UserAlreadyExistException;
import com.terron.models.company.Company;
import com.terron.models.company.CompanyType;
import com.terron.models.user.UserRole;
import com.terron.models.user.Users;
import com.terron.repository.company.CompanyRepository;
import com.terron.repository.user.UserRepository;
import com.terron.services.email.EmailServiceImpl;
import com.terron.services.utils.PaginationModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService{

    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    Random random;

    @Override
    @Transactional
    public Company onboardCompany(OnboardCompanyDto onboardCompanyDto) throws Exception {
        Boolean companyExists = companyRepository.existsByOfficialEmailAddress(onboardCompanyDto.getOfficialEmailAddress());
        if (companyExists) {
            throw new UserAlreadyExistException(String.format("Company with email address: %s already exists", onboardCompanyDto.getOfficialEmailAddress()));

        }
        Boolean companyExistsByCac = companyRepository.existsByCacNumber(onboardCompanyDto.getCacNumber());
        if (companyExistsByCac) {
            throw new UserAlreadyExistException(String.format("Company with cac number: %s already exists", onboardCompanyDto.getCacNumber()));

        }
        Company company = modelMapper.map(onboardCompanyDto, Company.class);
        company.setRegisteredDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")));
        company = companyRepository.save(company);
        String verificationToken = String.format("%04d", random.nextInt(10000));
        Users user = Users.builder()
                .emailAddress(onboardCompanyDto.getOfficialEmailAddress())
                .registeredDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
                .role(UserRole.COMPANY_OWNER)
                .firstName(onboardCompanyDto.getContactPersonFirstname())
                .lastName(onboardCompanyDto.getContactPersonLastname())
                .phoneNumber(company.getContactPersonPhoneNumber())
                .companyId(company.getId())
                .verificationToken(verificationToken)
                .modifiedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")))
                .build();
        userRepository.save(user);
        sendConfirmationMail(user, "localhost:3000");
        return company;
    }

    @Override
    public Company addHotel(OnboardCompanyDto onboardCompanyDto) throws Exception {
       Company company = onboardCompany(onboardCompanyDto);
       return company;
    }

    public PaginationModel getAllHotels(Integer page, Integer pageSize, String searchField, String state){
        Page<Company> companies = null;
        Pageable pagination = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "registeredDate"));
        try {
            companies = searchField.length() > 0
                    ? companyRepository.findByCompanyTypeAndCompanyNameContainingOrCacNumberContaining(CompanyType.hotel, searchField, searchField, pagination)
                    : state.length() > 0
                    ? companyRepository.findByCompanyTypeAndState(CompanyType.hotel,state, pagination)
                    : companyRepository.findAllByCompanyType(CompanyType.hotel, pagination);

            PaginationModel paginationModel = new PaginationModel();
            paginationModel.setTotalCount(companies.getTotalElements());
            paginationModel.setData(companies.getContent());
            Long hotelCount = companyRepository.countAllByCompanyType(CompanyType.hotel);
            paginationModel.setTotalHotels(hotelCount);
            Long usersCount = userRepository.count();
            paginationModel.setTotalUsers(usersCount);

            return paginationModel;
        } finally {
            if (companies != null && companies instanceof Closeable) {
                try {
                    ((Closeable) companies).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendConfirmationMail(Users applicationUser, String url) throws Exception {
        String toAddress = applicationUser.getEmailAddress();
        String fromAddress = "o.ifeoluwah@gmail.com";
        String senderName = "Terror Insure";
        String subject = "Welcome to Terror insure";
        String verifyURL = url + "/verify?token=" + applicationUser.getVerificationToken();

        Context context = new Context();
        context.setVariable("name", applicationUser.getFirstName() + " " + applicationUser.getLastName());
        context.setVariable("link", verifyURL);

        String content = templateEngine.process("confirmationEmail", context);

        new EmailServiceImpl().sendNotification(fromAddress, senderName, toAddress, subject, verifyURL, content);
    }

}
