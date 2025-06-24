package ru.volodin.deal.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestConfig {

    @Bean(name = "calculatorClient")
    public RestClient calculatorRestClient(@Value("${client.calculator.url}") String calculatorBaseUrl) {
        return RestClient.builder()
                .baseUrl(calculatorBaseUrl)
                .build();
    }
}
`