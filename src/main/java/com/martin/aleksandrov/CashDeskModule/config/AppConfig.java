package com.martin.aleksandrov.CashDeskModule.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Value("${api.key}")
    public String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}
