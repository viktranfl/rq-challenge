package com.example.rqchallenge.employees.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EmployeeConfig {
    @Bean
    public RestTemplate restTemplateBean() {
        return new RestTemplate();
    }
}
