package ru.volodin.calculator.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "service")
@Getter
@Setter
public class ServiceProperties {

    private int rate;
    private Calculator calculator;
    private Insurance insurance;

    @Getter
    @Setter
    public static class Calculator {
        private int round;
    }

    @Getter
    @Setter
    public static class Insurance {
        private int cost;
    }
}
