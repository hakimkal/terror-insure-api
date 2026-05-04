package com.terron.controller.controllers;

import com.terron.services.email.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@Profile("dev")
@RestController
@RequestMapping("/v1/test")
public class TestEmailController {

    private final EmailService emailService;

    @Value("${app.mail.from-address}")
    private String fromAddress;

    public TestEmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-email")
    public ResponseEntity<?> sendTestEmail(@RequestParam String to) throws Exception {
        emailService.sendNotification(
                fromAddress,
                "Terron Test",
                to,
                "Test email from Terron",
                "",
                "<h1>It works!</h1><p>This is a test email sent via Resend.</p>"
        );
        return ResponseEntity.ok(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "message", "Test email sent to " + to,
                "status", "success"
        ));
    }
}
