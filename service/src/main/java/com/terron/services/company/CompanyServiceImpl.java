package com.terron.services.company;

import com.terron.dto.OnboardCompanyDto;
import com.terron.exceptions.UserAlreadyExistException;
import com.terron.models.company.Company;
import com.terron.models.user.UserRole;
import com.terron.models.user.Users;
import com.terron.repository.company.CompanyRepository;
import com.terron.repository.user.UserRepository;
import com.terron.services.email.EmailServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        Company company = modelMapper.map(onboardCompanyDto, Company.class);
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
