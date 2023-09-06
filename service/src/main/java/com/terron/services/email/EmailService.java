package com.terron.services.email;

import org.springframework.mail.SimpleMailMessage;

import javax.mail.MessagingException;

public interface EmailService {

    void sendEmail(SimpleMailMessage email);

    void sendConfirmationMail(String email, String token) throws MessagingException;

    void sendResetPasswordMail(String email, String token) throws MessagingException;

    }
