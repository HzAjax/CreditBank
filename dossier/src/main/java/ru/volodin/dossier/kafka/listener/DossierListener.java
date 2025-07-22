package ru.volodin.dossier.kafka.listener;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.volodin.dossier.kafka.dto.EmailMessage;
import ru.volodin.dossier.kafka.dto.EmailMessageWithCreditDto;
import ru.volodin.dossier.kafka.dto.EmailMessageWithSesCode;
import ru.volodin.dossier.service.DossierService;

@Slf4j
@Service
@RequiredArgsConstructor
public class DossierListener {
    private final DossierService dossierService;

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${kafka.topics.finishRegistration}")
    public void handleFinishRegistration(EmailMessage emailMessage) throws MessagingException {
        log.info("Received message from topic finishRegistration: {}", emailMessage);
        dossierService.sendMessageEmail(emailMessage);
    }

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${kafka.topics.createDocuments}")
    public void handleCreateDocuments(EmailMessage emailMessage) throws MessagingException {
        log.info("Received message from topic createDocuments: {}", emailMessage);
        dossierService.sendMessageEmail(emailMessage);
    }

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${kafka.topics.sendDocuments}")
    public void handleSendDocuments(EmailMessageWithCreditDto emailMessage) throws MessagingException {
        log.info("Received message from topic sendDocuments: {}", emailMessage);
        dossierService.sendMessageEmail(emailMessage);
    }

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${kafka.topics.sendSes}")
    public void handleSendSesCode(EmailMessageWithSesCode emailMessageWithSesCode) {
        log.info("Received message from topic sendSes: {}", emailMessageWithSesCode);
        dossierService.sendMessageEmail(emailMessageWithSesCode);
    }

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${kafka.topics.creditIssued}")
    public void handleCreditIssued(EmailMessage emailMessage) throws MessagingException {
        log.info("Received message from topic creditIssued: {}", emailMessage);
        dossierService.sendMessageEmail(emailMessage);
    }

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${kafka.topics.statementDenied}")
    public void handleStatementDenied(EmailMessage emailMessage) throws MessagingException {
        log.info("Received message from topic statementDenied: {}", emailMessage);
        dossierService.sendMessageEmail(emailMessage);
    }
}
