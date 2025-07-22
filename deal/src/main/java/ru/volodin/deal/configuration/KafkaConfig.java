package ru.volodin.deal.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import ru.volodin.deal.configuration.props.KafkaTopicsProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnExpression("#{${kafka.init.topics}==true}")
public class KafkaConfig {
    private final KafkaTopicsProperties topics;

    @Bean
    public NewTopic finishRegistrationTopic() {
        return TopicBuilder
                .name(topics.getFinishRegistration())
                .build();
    }

    @Bean
    public NewTopic createDocumentsTopic() {
        return TopicBuilder
                .name(topics.getCreateDocuments())
                .build();
    }

    @Bean
    public NewTopic sendDocumentsTopic() {
        return TopicBuilder
                .name(topics.getSendDocuments())
                .build();
    }

    @Bean
    public NewTopic sendSesTopic() {
        return TopicBuilder
                .name(topics.getSendSes())
                .build();
    }

    @Bean
    public NewTopic creditIssuedTopic() {
        return TopicBuilder
                .name(topics.getCreditIssued())
                .build();
    }

    @Bean
    public NewTopic statementDeniedTopic() {
        return TopicBuilder
                .name(topics.getStatementDenied())
                .build();
    }
}