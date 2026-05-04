package com.terron.services.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.host:smtp.resend.com}")
    private String mailHost;

    @Value("${spring.mail.port:465}")
    private int mailPort;

    @Value("${spring.mail.username:resend}")
    private String username;

    @Value("${spring.mail.password:}")
    private String password;

    private final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    @Override
    @Async
    public void sendNotification(String fromAddress, String senderName, String toAddress, String subject, String redirectLink, String content) throws Exception {
        Properties mailProp = mailSender.getJavaMailProperties();
        mailProp.setProperty("mail.mime.address.strict", "false");
        mailProp.put("mail.transport.protocol", "smtp");
        mailProp.put("mail.smtp.auth", "true");
        mailProp.put("mail.smtp.starttls.enable", "true");
        mailProp.put("mail.smtp.ssl.enable", "true");
        mailProp.put("mail.debug", "true");
        mailProp.put("mail.default-encoding", "UTF-8");

        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setFrom(fromAddress, senderName);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(content, true);
        } catch (Exception ex) {
            throw new Exception("There is a problem with the encoding of your message", ex);
        }

        mailSender.send(mimeMessage);
    }

}
