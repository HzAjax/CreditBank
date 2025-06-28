package ru.volodin.deal.configuration.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "client.calculator")
@Getter
@Setter
public class CalculatorClientProperties {
    private String url;
    private Path path = new Path();

    @Getter
    @Setter
    public static class Path {
        private String offers;
        private String calc;
    }
}
