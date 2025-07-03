package ru.volodin.statement.configuration.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "prescoring.age")
@Getter
@Setter
public class PrescoringProperties {
    private Integer minAge;
}
