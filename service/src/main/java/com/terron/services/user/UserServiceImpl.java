package com.terron.services.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terron.dto.ChangePasswordDto;
import com.terron.dto.RequestResetPasswordDto;
import com.terron.dto.UpdatePasswordDto;
import com.terron.dto.UpdateProfileDto;
import com.terron.dto.UserRegistrationDto;
import com.terron.exceptions.UserAlreadyExistException;
import com.terron.models.company.Company;
import com.terron.models.user.Users;
import com.terron.repository.company.CompanyRepository;
import com.terron.repository.hotels.GuestInsuranceRepository;
import com.terron.repository.hotels.ReservationsRepository;
import com.terron.repository.payment.PaymentRepository;
import com.terron.repository.user.UserRepository;
import com.terron.services.email.EmailService;
import org.springframework.beans.factory.annotation.Value;
import com.terron.services.utils.UserDetailsDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final CompanyRepository companyRepository;

    @Autowired
    ReservationsRepository reservationsRepository;

    @Autowired
    GuestInsuranceRepository guestInsuranceRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    Random random;

    @Autowired
    TemplateEngine templateEngine;

    private final EmailService emailService;

    @Value("${app.mail.from-address}")
    private String defaultFromAddress;

    private void sendWelcomeMail(Users user) throws Exception {
        String toAddress = user.getEmailAddress();
        String senderName = "Terror Insure";
        String subject = "Welcome to Terror insure";

        Context context = new Context();
        context.setVariable("name", user.getFirstName() + " " + user.getLastName());
        context.setVariable("code", user.getVerificationToken());

        String content = templateEngine.process("welcome", context);

        emailService.sendNotification(defaultFromAddress, senderName, toAddress, subject, "verifyURL", content);
    }

    @Override
    public void changePassword(ChangePasswordDto changePasswordDto) throws Exception {
        Users user = userRepository.findByEmailAddress(changePasswordDto.getEmailAddress()).orElseThrow(() -> new NotFoundException(String.format("User with this email: %s does not exist", changePasswordDto.getEmailAddress())));
        user.setPassword(encoder.encode(changePasswordDto.getPassword()));
        user.setModifiedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")));
        userRepository.save(user);
    }

    @Override
    public void confirmUser(String token) throws Exception {
        Users user = userRepository.findByVerificationToken(token).orElseThrow(() -> new NotFoundException(String.format("Invalid token: %s", token)));
        user.setIsActive(true);
        user.setIsVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
        sendWelcomeMail(user);
    }


    private void sendConfirmResetPasswordEmail(Users user, String url) throws Exception {
        String toAddress = user.getEmailAddress();
        String senderName = "Terror";
        String subject = "Welcome to Terror insure";
        String verifyURL = url + "/verify?token=" + user.getVerificationToken();

        Context context = new Context();
        context.setVariable("name", user.getFirstName() + " " + user.getLastName());
        context.setVariable("link", verifyURL);

        String content = templateEngine.process("resetPasswordConfirmation", context);

        emailService.sendNotification(defaultFromAddress, senderName, toAddress, subject, verifyURL, content);
    }


    @Override
    public void confirmResetPassword(String token, UpdatePasswordDto updatePasswordDto) throws Exception {
        Users user = userRepository.findByVerificationToken(token).orElseThrow(() -> new NotFoundException(String.format("Invalid token: %s", token)));
        user.setPassword(encoder.encode(updatePasswordDto.getNewPassword()));
        user.setVerificationToken(null);
        userRepository.save(user);
        sendConfirmResetPasswordEmail(user, "localhost:3000");
    }

    private void sendResetPasswordEmail(Users user, String url) throws Exception {
        String toAddress = user.getEmailAddress();
        String senderName = "Terror insure";
        String subject = "Reset your password";
        String verifyURL = url + "/verify?token=" + user.getVerificationToken();

        Context context = new Context();
        context.setVariable("name", user.getFirstName() + " " + user.getLastName());
        context.setVariable("link", verifyURL);

        String content = templateEngine.process("resetPassword", context);

        emailService.sendNotification(defaultFromAddress, senderName, toAddress, subject, verifyURL, content);
    }

    @Override
    public Users resetPassword(RequestResetPasswordDto passwordDto) throws Exception {
        Users user = userRepository.findByEmailAddress(passwordDto.getEmailAddress()).orElseThrow(() -> new NotFoundException(String.format("User with this email: %s does not exist", passwordDto.getEmailAddress())));
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        userRepository.save(user);
        sendResetPasswordEmail(user, "localhost:3000");
        return user;
    }

    public UserDetailsDto getUserById(Long id) throws NotFoundException {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with this id: %s does not exist", id)));
        Company company = new Company();
        if(user.getCompanyId() != null){
            company = companyRepository.findById(user.getCompanyId()).get();
        }
        return UserDetailsDto.builder()
                .user(user)
                .company(company)
                .build();
    }

    public UserDetailsDto getUserByToken(String token) throws NotFoundException {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String payload = new String(decoder.decode(chunks[1]));
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String subject = jsonNode.get("sub").asText();
        Users user = userRepository.findByEmailAddress(subject)
                .orElseThrow(() -> new NotFoundException(String.format("User with this id: %s does not exist", subject)));
        Company company = companyRepository.findById(user.getCompanyId()).get();
        return UserDetailsDto.builder()
                .user(user)
                .company(company)
                .build();
    }

    public Users registerUser(UserRegistrationDto userRegistrationDto) throws Exception {
        Boolean userExists = userRepository.existsByEmailAddress(userRegistrationDto.getEmailAddress());
        if (userExists) {
            throw new UserAlreadyExistException(String.format("User with email address: %s already exists", userRegistrationDto.getEmailAddress()));

        }
        Users user = modelMapper.map(userRegistrationDto, Users.class);
        user.setPassword(encoder.encode(userRegistrationDto.getPassword()));
        user.setIsVerified(true);
        user.setIsActive(true);
        user.setModifiedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")));
        user.setRegisteredDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")));
        user = userRepository.save(user);
        return user;
    }

    public Users updateUser(UpdateProfileDto updateProfileDto, Long userId, Long companyId) throws Exception {
        boolean userExists = userRepository.existsById(userId);
        if (!userExists) {
            throw new UserAlreadyExistException(String.format("User with id: %s does not exist", userId));

        }

        if(updateProfileDto.getEmailAddress() != null) {
            boolean userExistsByEmail = userRepository.existsByEmailAddress(updateProfileDto.getEmailAddress());
            if (!userExistsByEmail) {
                throw new UserAlreadyExistException(String.format("User with email: %s already exist", updateProfileDto.getEmailAddress()));

            }
        }

        Users user = userRepository.findById(userId).get();
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        mapper.map(updateProfileDto, user);
        if (companyId != 0) {
            boolean companyExists = companyRepository.existsById(companyId);
            if (!companyExists) {
                throw new UserAlreadyExistException(String.format("Company with id: %s does not exist", companyId));

            }
            user.setCompanyId(companyId);
        }

        user.setModifiedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")));
        user = userRepository.save(user);
        return user;
    }



}