package ru.volodin.deal.configuration.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "client.calculator")
@Getter
@Setter
public class CalculatorClientProperties {

    private String url;
}
