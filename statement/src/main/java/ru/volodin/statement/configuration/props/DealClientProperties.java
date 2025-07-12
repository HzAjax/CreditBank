package ru.volodin.statement.configuration.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "client.deal")
@Getter
@Setter
public class DealClientProperties {
    private String url;
    private Path path = new Path();

    @Getter
    @Setter
    public static class Path {
        private String statement;
        private String select;
    }
}
