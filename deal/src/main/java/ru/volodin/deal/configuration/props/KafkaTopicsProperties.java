package ru.volodin.deal.configuration.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kafka.topics")
public class KafkaTopicsProperties {
    private String finishRegistration;
    private String createDocuments;
    private String sendDocuments;
    private String sendSes;
    private String creditIssued;
    private String statementDenied;
}
