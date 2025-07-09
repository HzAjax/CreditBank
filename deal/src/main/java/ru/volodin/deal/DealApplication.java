package ru.volodin.deal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import ru.volodin.deal.configuration.props.CalculatorClientProperties;
import ru.volodin.deal.configuration.props.OfferRetryProperties;

@SpringBootApplication(scanBasePackages = {
        "ru.volodin.deal",
        "org.example.mylib"
})
@EnableConfigurationProperties({CalculatorClientProperties.class, OfferRetryProperties.class})
@EnableRetry
public class DealApplication {
    public static void main(String[] args) {
        SpringApplication.run(DealApplication.class, args);
    }
}
