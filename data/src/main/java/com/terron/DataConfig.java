package com.terron;

import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@ComponentScan(basePackages = "com.terron")
@EntityScan(basePackages = "com.terron")
@Configuration
@EnableAutoConfiguration
public class DataConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
