package com.terron.services.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

import static com.terron.services.utils.UrlConstant.REGISTER_URL;
import static com.terron.services.utils.UrlConstant.RESET_URL;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    JavaMailSender javaMailSender;


    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

    public void sendConfirmationMail(String email, String token) throws MessagingException {
        final SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(email);
        mailMessage.setSubject("Mail Confirmation Link!");
        mailMessage.setFrom("o.ifeoluwah@gmail.com");
        mailMessage.setText(
                "Thank you for registering. Please click on the below link to activate your account." +
                        REGISTER_URL + token);
        sendEmail(mailMessage);
    }
    public void sendResetPasswordMail(String email, String token) throws MessagingException {
        final SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(email);
        mailMessage.setSubject("Reset Password Confirmation Link!");
        mailMessage.setFrom("o.ifeoluwah@gmail.com");
        mailMessage.setText(
                "Please click on the below link to reset your password.\n\t" +
                        RESET_URL + token);
        sendEmail(mailMessage);
    }

}