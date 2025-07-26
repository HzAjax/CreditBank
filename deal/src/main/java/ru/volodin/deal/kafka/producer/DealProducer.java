package ru.volodin.deal.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import ru.volodin.deal.configuration.props.KafkaTopicsProperties;
import ru.volodin.deal.entity.dto.api.CreditDto;
import ru.volodin.deal.kafka.dto.EmailMessage;
import ru.volodin.deal.kafka.dto.EmailMessageCreditDto;
import ru.volodin.deal.kafka.dto.EmailMessageSesCode;
import ru.volodin.deal.kafka.dto.enums.Theme;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealProducer {
    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopics;

    public void sendFinishRegistrationRequestNotification(String email, Theme theme, UUID statementId) {
        sendNotification(email, kafkaTopics.getFinishRegistration(), theme, statementId);
    }

    private void sendNotification(String email, String topic, Theme theme, UUID statementId) {
        Message<EmailMessage> message = MessageBuilder
                .withPayload(new EmailMessage(email, theme, statementId))
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();
        kafkaTemplate.send(message);
    }

    public void sendPrepareDocumentsNotification(String email, Theme theme, UUID statementId, CreditDto creditDto) {
        Message<EmailMessageCreditDto> message = MessageBuilder
                .withPayload(new EmailMessageCreditDto(email, theme, statementId, creditDto))
                .setHeader(KafkaHeaders.TOPIC, kafkaTopics.getSendDocuments())
                .build();
        kafkaTemplate.send(message);
    }

    public void sendSignCodeDocumentsNotification(String email, Theme theme, UUID statementId, UUID sesCode) {
        Message<EmailMessageSesCode> message = MessageBuilder
                .withPayload(new EmailMessageSesCode(email, theme, statementId, sesCode))
                .setHeader(KafkaHeaders.TOPIC, kafkaTopics.getSendSes())
                .build();
        kafkaTemplate.send(message);
    }


    public void sendSuccessSignDocumentsNotification(String email, Theme theme, UUID statementId) {
        sendNotification(email, kafkaTopics.getCreditIssued(), theme, statementId);
    }

    public void sendScoringException(String email, Theme theme, UUID statementId) {
        sendNotification(email, kafkaTopics.getStatementDenied(), theme, statementId);
    }


    public void sendCreateDocumentsNotification(String email, Theme theme, UUID statementId) {
        sendNotification(email, kafkaTopics.getCreateDocuments(), theme, statementId);
    }
}
