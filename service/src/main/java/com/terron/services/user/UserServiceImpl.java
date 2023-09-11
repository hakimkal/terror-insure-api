package com.terron.services.user;

import com.terron.dto.*;
import com.terron.exceptions.UserAlreadyExistException;
import com.terron.models.user.Users;
import com.terron.models.user.UserRole;
import com.terron.repository.user.UserRepository;
import com.terron.services.email.EmailServiceImpl;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    @Autowired
    ModelMapper modelMapper;

    private final EmailServiceImpl emailServiceImpl;

    @Autowired
    Random random;

    @Autowired
    TemplateEngine templateEngine;

    @Override
    public void registerUser(UserRegistrationDto userRegistrationDto) throws Exception {
        Users user = new Users();
        userRegistrationDto.setEmailAddress(userRegistrationDto.getEmailAddress().toLowerCase());
        boolean userExists = userRepository
                .existsByEmailAddress(userRegistrationDto.getEmailAddress());

        if (userExists) {
            throw new UserAlreadyExistException(String.format("User with email address: %s already exists", userRegistrationDto.getEmailAddress()));

        }
        String verificationToken = String.format("%04d", random.nextInt(10000));
        userRegistrationDto.setPassword(encoder.encode(userRegistrationDto.getPassword()));
        user = modelMapper.map(userRegistrationDto, Users.class);
        user.setIsActive(false);
        user.setIsVerified(false);
        user.setRole(UserRole.COMPANY_OWNER);
        user.setVerificationToken(verificationToken);
        user.setModifiedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")));
        user.setRegisteredDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")));
        userRepository.save(user);

        sendWelcomeMail(user, "localhost:3000");
    }

    private void sendWelcomeMail(Users applicationUser, String url) throws Exception {
        String toAddress = applicationUser.getEmailAddress();
        String fromAddress = "o.ifeoluwah@gmail.com";
        String senderName = "Terror Insure";
        String subject = "Welcome to Terror insure";

        Context context = new Context();
        context.setVariable("name", applicationUser.getEmailAddress());
        context.setVariable("code", applicationUser.getVerificationToken());

        String content = templateEngine.process("welcome", context);

        new EmailServiceImpl().sendNotification(fromAddress, senderName, toAddress, subject, "verifyURL", content);
    }

    @Override
    public void confirmUser(String token) throws Exception {
        Users user = userRepository.findByVerificationToken(token).orElseThrow(() -> new NotFoundException(String.format("Invalid token: %s", token)));
        user.setIsActive(true);
        user.setIsVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
        sendConfirmationMail(user, "localhost:3000");
    }

    private void sendConfirmationMail(Users applicationUser, String url) throws Exception {
        String toAddress = applicationUser.getEmailAddress();
        String fromAddress = "o.ifeoluwah@gmail.com";
        String senderName = "Terror Insure";
        String subject = "Welcome to Terror insure";
        String verifyURL = url + "/verify?token=" + applicationUser.getVerificationToken();

        Context context = new Context();
        context.setVariable("name", applicationUser.getEmailAddress());
        context.setVariable("link", verifyURL);

        String content = templateEngine.process("confirmationEmail", context);

        new EmailServiceImpl().sendNotification(fromAddress, senderName, toAddress, subject, verifyURL, content);
    }

    private void sendConfirmResetPasswordEmail(Users user, String url) throws Exception {
        String toAddress = user.getEmailAddress();
        String fromAddress = "o.ifeoluwah@gmail.com";
        String senderName = "Terror";
        String subject = "Welcome to Terror insure";
        String verifyURL = url + "/verify?token=" + user.getVerificationToken();

        Context context = new Context();
        context.setVariable("name", user.getEmailAddress());
        context.setVariable("link", verifyURL);

        String content = templateEngine.process("resetPasswordConfirmation", context);

        new EmailServiceImpl().sendNotification(fromAddress, senderName, toAddress, subject, verifyURL, content);
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
        String fromAddress = "o.ifeoluwah@gmail.com";
        String senderName = "Terror insure";
        String subject = "Reset your password";
        String verifyURL = url + "/verify?token=" + user.getVerificationToken();

        Context context = new Context();
        context.setVariable("name", user.getEmailAddress());
        context.setVariable("link", verifyURL);

        String content = templateEngine.process("resetPassword", context);

        new EmailServiceImpl().sendNotification(fromAddress, senderName, toAddress, subject, verifyURL, content);
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

}
