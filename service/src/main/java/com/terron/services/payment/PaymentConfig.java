package com.terron.services.payment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PaymentConfig {

    @Bean
    public WebClient payOnUsWebClient() {
        return WebClient.builder()
                .build();
    }
}

