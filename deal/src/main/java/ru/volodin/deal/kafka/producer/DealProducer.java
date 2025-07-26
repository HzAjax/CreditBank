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

    public void sendFinishRegistrationRequestNotification(String email, UUID statementId) {
        Message<EmailMessage> message = MessageBuilder
                .withPayload(new EmailMessage(email, Theme.FINISH_REGISTRATION, statementId))
                .setHeader(KafkaHeaders.TOPIC, kafkaTopics.getFinishRegistration())
                .build();
        sendAndWait(message);
    }

    public void sendPrepareDocumentsNotification(String email, Theme theme, UUID statementId, CreditDto creditDto) {
        Message<EmailMessageCreditDto> message = MessageBuilder
                .withPayload(new EmailMessageCreditDto(email, theme, statementId, creditDto))
                .setHeader(KafkaHeaders.TOPIC, kafkaTopics.getSendDocuments())
                .build();
        sendAsync(message);
    }

    public void sendSignCodeDocumentsNotification(String email, Theme theme, UUID statementId, UUID sesCode) {
        Message<EmailMessageSesCode> message = MessageBuilder
                .withPayload(new EmailMessageSesCode(email, theme, statementId, sesCode))
                .setHeader(KafkaHeaders.TOPIC, kafkaTopics.getSendSes())
                .build();
        sendAsync(message);
    }

    public void sendSuccessSignDocumentsNotification(String email, Theme theme, UUID statementId) {
        Message<EmailMessage> message = MessageBuilder
                .withPayload(new EmailMessage(email, theme, statementId))
                .setHeader(KafkaHeaders.TOPIC, kafkaTopics.getCreditIssued())
                .build();
        sendAsync(message);
    }

    public void sendCreateDocumentsNotification(String email, Theme theme, UUID statementId) {
        Message<EmailMessage> message = MessageBuilder
                .withPayload(new EmailMessage(email, theme, statementId))
                .setHeader(KafkaHeaders.TOPIC, kafkaTopics.getCreateDocuments())
                .build();
        sendAsync(message);
    }

    private void sendAndWait(Message<?> message) {
        try {
            kafkaTemplate.send(message).get();
            log.debug("Kafka message sent: {}", message);
        } catch (Exception e) {
            log.error("Kafka send failed: {}", message, e);
            throw new RuntimeException("Ошибка при отправке Kafka-сообщения", e);
        }
    }

    private void sendAsync(Message<?> message) {
        kafkaTemplate.send(message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka send failed async: {}", message, ex);
                    } else {
                        log.debug("Kafka message sent: {}", message);
                    }
                });
    }
}
