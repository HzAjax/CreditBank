package ru.volodin.dossier.service;

import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.volodin.dossier.kafka.dto.EmailMessage;
import ru.volodin.dossier.kafka.dto.EmailMessageCreditDto;
import ru.volodin.dossier.kafka.dto.EmailMessageSesCode;
import ru.volodin.dossier.kafka.dto.enums.Theme;
import ru.volodin.dossier.service.provider.DocumentGenerator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DossierService {

    private final JavaMailSender sender;
    private final DocumentGenerator documentGenerator;
    private final SpringTemplateEngine templateEngine;

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

        String text = getEmailHtml(emailMessage);
        helper.setText(text, true);

        sender.send(message);
        log.info("Email sent successfully to address: {}", emailMessage.getAddress());
    }

    public void sendMessageEmail(EmailMessageCreditDto emailMessage) throws MessagingException {
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

    public void sendMessageEmail(EmailMessageSesCode emailMessage) {
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
        } catch (Exception  e) {
            log.error("Failed to send SES code email", e);
            throw new RuntimeException("Ошибка при отправке email", e);
        }
    }

    private String getEmailHtml(EmailMessage emailMessage) {

        if (emailMessage.getTheme() == null) {
            throw new IllegalStateException("NULL THEME! Full message: " + emailMessage);
        }

        if (emailMessage.getTheme() == Theme.CREATED_DOCUMENTS) {
            return generateSendDocumentEmail(emailMessage.getStatementId());
        }

        Context context = new Context();
        context.setVariable("message", emailMessage);

        String template = switch (emailMessage.getTheme()) {
            case FINISH_REGISTRATION -> "finishRegistration";
            case CC_DENIED -> "ccDenied";
            case CC_APPROVED -> "ccApproved";
            case PREPARE_DOCUMENTS -> "prepareDocuments";
            case SIGN_DOCUMENTS -> "signDocuments";
            default -> "defaultNotification";
        };

        return templateEngine.process(template, context);
    }

    private String generateSendDocumentEmail(UUID statementId) {
        try {
            var resource = new ClassPathResource("templates/documentSend.html");
            String template = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
            return template.formatted(String.format(sendDocumentUrlTemplate, statementId));
        } catch (IOException | RuntimeException  e) {
            log.error("Error reading documentSend.html", e);
            throw new RuntimeException("Ошибка при чтении шаблона send", e);
        }
    }

    private String generateSignDocumentEmail(UUID statementId) {
        try {
            var resource = new ClassPathResource("templates/documentSign.html");
            String template = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
            return template.formatted(String.format(signDocumentUrlTemplate, statementId));
        } catch (IOException | RuntimeException  e) {
            log.error("Error reading documentSign.html", e);
            throw new RuntimeException("Ошибка при чтении шаблона sign", e);
        }
    }

    private String generateCodeDocumentEmail(UUID statementId, UUID sesCode) {
        try {
            var resource = new ClassPathResource("templates/documentCode.html");
            String template = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
            return template
                    .replace("%s", String.format(codeDocumentUrlTemplate, statementId))
                    .replace("SesCode", sesCode.toString());
        } catch (IOException | RuntimeException  e) {
            log.error("Error reading documentCode.html", e);
            throw new RuntimeException("Ошибка при генерации шаблона code", e);
        }
    }
}

