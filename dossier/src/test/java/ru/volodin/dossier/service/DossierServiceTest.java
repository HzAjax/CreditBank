package ru.volodin.dossier.service;

import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
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
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DossierServiceTest {

    @Mock(lenient = true)
    private JavaMailSender mailSender;
    @Mock
    private DocumentGenerator documentGenerator;
    @Mock
    private SpringTemplateEngine templateEngine;
    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private DossierService dossierService;

    @BeforeEach
    void setUp() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        ReflectionTestUtils.setField(dossierService, "sendDocumentUrlTemplate", "http://send/%s");
        ReflectionTestUtils.setField(dossierService, "signDocumentUrlTemplate", "http://sign/%s");
        ReflectionTestUtils.setField(dossierService, "codeDocumentUrlTemplate", "http://code/%s");
    }

    @BeforeEach
    void restoreValidTemplates() throws IOException {
        Path base = Path.of("build/resources/test/templates");
        Files.createDirectories(base);

        Files.writeString(base.resolve("documentSend.html"),
                "Документы доступны по ссылке: %s", StandardCharsets.UTF_8);

        Files.writeString(base.resolve("documentSign.html"),
                "Подпишите документы: %s", StandardCharsets.UTF_8);

        Files.writeString(base.resolve("documentCode.html"),
                "Код SES: SesCode, ссылка: %s", StandardCharsets.UTF_8);
    }

    @Test
    void testSendSimpleEmail_success() throws Exception {
        EmailMessage msg = new EmailMessage();
        msg.setAddress("test@example.com");
        msg.setTheme(Theme.CC_APPROVED);

        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Email</html>");

        dossierService.sendMessageEmail(msg);

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void testSendEmailWithCredit_documentGenerationFails() {
        EmailMessageCreditDto msg = new EmailMessageCreditDto();
        msg.setAddress("test@example.com");
        msg.setStatementId(UUID.randomUUID());

        when(documentGenerator.generateDocument(msg)).thenThrow(new RuntimeException("Fail"));

        MessagingException ex = assertThrows(MessagingException.class, () ->
                dossierService.sendMessageEmail(msg));

        assertTrue(ex.getMessage().contains("Error generating document"));
    }

    @Test
    void testSendEmailWithSesCode_success() {
        EmailMessageSesCode msg = new EmailMessageSesCode();
        msg.setAddress("test@example.com");
        msg.setStatementId(UUID.randomUUID());
        msg.setSesCodeConfirm(UUID.randomUUID());

        assertDoesNotThrow(() -> dossierService.sendMessageEmail(msg));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void testSendEmailWithSesCode_mailSenderFails() {
        EmailMessageSesCode msg = new EmailMessageSesCode();
        msg.setAddress("fail@example.com");
        msg.setStatementId(UUID.randomUUID());
        msg.setSesCodeConfirm(UUID.randomUUID());

        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Boom"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                dossierService.sendMessageEmail(msg));
        assertTrue(ex.getMessage().contains("Ошибка при отправке email"));
    }

    @ParameterizedTest
    @EnumSource(Theme.class)
    void testGetEmailHtml_byTheme(Theme theme) {
        EmailMessage msg = new EmailMessage();
        msg.setAddress("test@example.com");
        msg.setTheme(theme);
        msg.setStatementId(UUID.randomUUID());

        if (theme != Theme.CREATED_DOCUMENTS) {
            when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>OK</html>");
        }

        String html = ReflectionTestUtils.invokeMethod(dossierService, "getEmailHtml", msg);
        assertNotNull(html);
    }

    @Test
    void testSendEmailWithCredit_success() throws Exception {
        ReflectionTestUtils.setField(dossierService, "signDocumentUrlTemplate", "http://sign/%s");
        EmailMessageCreditDto msg = new EmailMessageCreditDto();
        msg.setAddress("test@example.com");
        msg.setStatementId(UUID.randomUUID());

        DataSource dataSource = mock(DataSource.class);
        when(documentGenerator.generateDocument(msg)).thenReturn(dataSource);

        dossierService.sendMessageEmail(msg);

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void testSendSimpleEmail_nullTheme_throwsIllegalStateException() {
        EmailMessage msg = new EmailMessage();
        msg.setAddress("test@example.com");
        msg.setTheme(null);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                dossierService.sendMessageEmail(msg)
        );

        assertTrue(ex.getMessage().contains("NULL THEME"));
    }

    @Test
    void testGenerateSendDocumentEmail_brokenFormat_throwsRuntimeException() throws IOException {
        Path path = Path.of("build/resources/test/templates/documentSend.html");
        Files.createDirectories(path.getParent());
        Files.writeString(path, "<p>URL 1: %s, URL 2: %s</p>", StandardCharsets.UTF_8);

        UUID statementId = UUID.randomUUID();

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ReflectionTestUtils.invokeMethod(dossierService, "generateSendDocumentEmail", statementId)
        );

        assertTrue(ex.getMessage().contains("Ошибка при чтении шаблона send"));
    }

    @Test
    void testGenerateSignDocumentEmail_brokenFormat_throwsRuntimeException() throws IOException {
        Path path = Path.of("build/resources/test/templates/documentSign.html");
        Files.createDirectories(path.getParent());
        Files.writeString(path, "<p>SIGN: %s, AGAIN: %s</p>", StandardCharsets.UTF_8);

        UUID statementId = UUID.randomUUID();

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ReflectionTestUtils.invokeMethod(dossierService, "generateSignDocumentEmail", statementId)
        );

        assertTrue(ex.getMessage().contains("Ошибка при чтении шаблона sign"));
    }

    @Test
    void testGenerateCodeDocumentEmail_missingFile_throwsRuntimeException() throws IOException {
        Path path = Path.of("build/resources/test/templates/documentCode.html");
        Files.write(path, new byte[]{ (byte) 0xC3, (byte) 0x28 }); // invalid UTF-8

        UUID statementId = UUID.randomUUID();
        UUID sesCode = UUID.randomUUID();

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ReflectionTestUtils.invokeMethod(dossierService, "generateCodeDocumentEmail", statementId, sesCode)
        );

        assertTrue(ex.getMessage().contains("Ошибка при генерации шаблона code"));
    }
}
