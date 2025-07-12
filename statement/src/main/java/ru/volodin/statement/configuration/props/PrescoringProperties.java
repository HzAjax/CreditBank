package ru.volodin.statement.configuration.props;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "prescoring.age")
@Getter
@Setter
public class PrescoringProperties {

    @NotNull(message = "minAge must be defined in application.yml")
    private Integer minAge;
}
