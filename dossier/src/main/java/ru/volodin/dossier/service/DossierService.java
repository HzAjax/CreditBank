package ru.volodin.dossier.service;

import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.volodin.dossier.kafka.dto.EmailMessage;
import ru.volodin.dossier.kafka.dto.EmailMessageWithCreditDto;
import ru.volodin.dossier.kafka.dto.EmailMessageWithSesCode;
import ru.volodin.dossier.service.provider.DocumentGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DossierService {
    private final JavaMailSender sender;
    private final DocumentGenerator documentGenerator;

    @Value("${client.deal.send}")
    private String sendDocumentUrlTemplate;
    @Value("${client.deal.sign}")
    private String signDocumentUrlTemplate;
    @Value("${client.deal.code}")
    private String codeDocumentUrlTemplate;


    public void sendMessageEmail(EmailMessage emailMessage) throws MessagingException {
        log.info("Preparing to send simple email for EmailMessage: {}", emailMessage);

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(emailMessage.getAddress());
        helper.setSubject("Кредитные уведомления");

        String text = getEmailText(emailMessage);
        helper.setText(text, true);

        sender.send(message);
        log.info("Email sent successfully to address: {}", emailMessage.getAddress());
    }

    public void sendMessageEmail(EmailMessageWithCreditDto emailMessage) throws MessagingException {
        log.info("Preparing to send email with credit documents for EmailMessageWithCreditDto: {}", emailMessage);

        DataSource dataSource;
        try {
            dataSource = documentGenerator.generateDocument(emailMessage);
        } catch (Exception e) {
            log.error("Failed to generate document", e);
            throw new MessagingException("Error generating document", e);
        }

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(emailMessage.getAddress());
        helper.setSubject("Кредитные уведомления");
        helper.addAttachment("documents.pdf", dataSource);
        helper.setText(generateSignDocumentEmail(emailMessage.getStatementId()), true);

        sender.send(message);
        log.info("Email with credit documents sent to {}", emailMessage.getAddress());
    }

    public void sendMessageEmail(EmailMessageWithSesCode emailMessage) {
        log.info("Preparing to send email with SES code for EmailMessageWithSesCode: {}", emailMessage);
        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(emailMessage.getAddress());
            helper.setSubject("Подтверждение SES-кода");

            String emailContent = generateCodeDocumentEmail(emailMessage.getStatementId(), emailMessage.getSesCodeConfirm());
            helper.setText(emailContent, true);

            sender.send(mimeMessage);
            log.info("Email with SES code sent to {}", emailMessage.getAddress());
        } catch (MessagingException e) {
            log.error("Failed to send SES code email", e);
            throw new RuntimeException("Ошибка при отправке email", e);
        }
    }

    private String getEmailText(EmailMessage emailMessage) {
        return switch (emailMessage.getTheme()) {
            case FINISH_REGISTRATION -> "Завершите регистрацию для получения кредита";
            case CC_DENIED -> "В кредите отказано";
            case CC_APPROVED -> "Кредит одобрен";
            case CREATED_DOCUMENTS -> generateSendDocumentEmail(emailMessage.getStatementId());
            case PREPARE_DOCUMENTS -> "Документы формируются";
            case SIGN_DOCUMENTS -> "Поздравляем! Документы подписаны, можете пользоваться кредитом";
            default -> "Уведомление о кредите";
        };
    }

    private String generateSendDocumentEmail(UUID statementId) {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("templates/documentSend.html")) {
            if (stream == null) throw new RuntimeException("Template not found: documentSend.html");
            String template = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return template.formatted(String.format(sendDocumentUrlTemplate, statementId));
        } catch (IOException e) {
            log.error("Error reading or processing HTML template", e);
            throw new RuntimeException("Ошибка при чтении HTML-шаблона", e);
        }
    }

    private String generateSignDocumentEmail(UUID statementId) {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("templates/documentSign.html")) {
            if (stream == null) throw new RuntimeException("Template not found: documentSign.html");
            String template = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return template.formatted(String.format(signDocumentUrlTemplate, statementId));
        } catch (IOException e) {
            log.error("Error reading or processing HTML template", e);
            throw new RuntimeException("Ошибка при чтении HTML-шаблона", e);
        }
    }

    private String generateCodeDocumentEmail(UUID statementId, UUID sesCode) {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("templates/documentCode.html")) {
            if (stream == null) throw new RuntimeException("Template not found: documentCode.html");
            String template = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return template
                    .replace("%s", String.format(codeDocumentUrlTemplate, statementId))
                    .replace("SesCode", sesCode.toString());
        } catch (IOException e) {
            log.error("Error reading or processing HTML template", e);
            throw new RuntimeException("Ошибка при генерации содержимого email", e);
        }
    }
}

