package com.terron;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.TemplateEngine;

import java.util.Random;


@SpringBootApplication
public class TerronApplication {
    public static void main(String[] args) {
        SpringApplication.run(TerronApplication.class, args);
    }

    @Bean
    public TemplateEngine templateEngine(){
        return new TemplateEngine();
    }

    @Bean
    public Random random(){
        return new Random();
    }
}
