package ru.volodin.deal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.volodin.deal.configuration.props.CalculatorClientProperties;
import ru.volodin.deal.configuration.props.OfferRetryProperties;

@SpringBootApplication
@EnableConfigurationProperties({CalculatorClientProperties.class, OfferRetryProperties.class})
public class DealApplication {
    public static void main(String[] args) {
        SpringApplication.run(DealApplication.class, args);
    }
}
