package com.terron.services.email;

public interface EmailService {

    void sendNotification(String fromAddress, String senderName, String toAddress, String subject, String redirectLink, String content) throws Exception;

    }
