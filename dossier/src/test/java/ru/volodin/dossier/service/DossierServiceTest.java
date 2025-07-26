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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import ru.volodin.dossier.kafka.dto.EmailMessage;
import ru.volodin.dossier.kafka.dto.EmailMessageCreditDto;
import ru.volodin.dossier.kafka.dto.EmailMessageSesCode;
import ru.volodin.dossier.kafka.dto.enums.Theme;
import ru.volodin.dossier.service.provider.DocumentGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DossierServiceTest {

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private DocumentGenerator documentGenerator;
    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private DossierService dossierService;

    @BeforeEach
    void setup() {
        lenient().when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        ReflectionTestUtils.setField(dossierService, "sendDocumentUrlTemplate", "http://send/%s");
        ReflectionTestUtils.setField(dossierService, "signDocumentUrlTemplate", "http://sign/%s");
        ReflectionTestUtils.setField(dossierService, "codeDocumentUrlTemplate", "http://code/%s");
    }

    @Test
    void testSendSimpleEmail_success() throws Exception {
        EmailMessage msg = new EmailMessage();
        msg.setAddress("test@example.com");
        msg.setTheme(Theme.CC_APPROVED);

        assertDoesNotThrow(() -> dossierService.sendMessageEmail(msg));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void testSendEmailWithCredit_success() throws Exception {
        EmailMessageCreditDto msg = new EmailMessageCreditDto();
        msg.setAddress("test@example.com");
        msg.setStatementId(UUID.randomUUID());

        DataSource mockDataSource = mock(DataSource.class);
        when(documentGenerator.generateDocument(msg)).thenReturn(mockDataSource);

        assertDoesNotThrow(() -> dossierService.sendMessageEmail(msg));
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void testSendEmailWithCredit_documentGenerationFails_throwsMessagingException() throws Exception {
        EmailMessageCreditDto msg = new EmailMessageCreditDto();
        msg.setAddress("test@example.com");
        msg.setStatementId(UUID.randomUUID());

        when(documentGenerator.generateDocument(msg)).thenThrow(new RuntimeException("Doc gen error"));

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
    void testSendEmailWithSesCode_exceptionThrown_runtimeException() {
        EmailMessageSesCode msg = new EmailMessageSesCode();
        msg.setAddress("fail@example.com");
        msg.setStatementId(UUID.randomUUID());
        msg.setSesCodeConfirm(UUID.randomUUID());

        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Boom"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                dossierService.sendMessageEmail(msg));
        assertTrue(ex.getMessage().contains("Ошибка при отправке email"));
    }

    @Test
    void testGenerateCodeDocumentEmail_success() {
        UUID statementId = UUID.randomUUID();
        UUID sesCode = UUID.randomUUID();

        String result = ReflectionTestUtils.invokeMethod(
                dossierService,
                "generateCodeDocumentEmail",
                statementId,
                sesCode
        );

        assertTrue(result.contains(statementId.toString()));
        assertTrue(result.contains(sesCode.toString()));
    }

    @ParameterizedTest
    @EnumSource(value = Theme.class)
    void testGetEmailText_byTheme(Theme theme) {
        EmailMessage msg = new EmailMessage();
        msg.setAddress("test@example.com");
        msg.setTheme(theme);
        msg.setStatementId(UUID.randomUUID());

        String result = ReflectionTestUtils.invokeMethod(dossierService, "getEmailText", msg);
        assertNotNull(result);
    }

    @Test
    void testGenerateSendDocumentEmail_throwsIOException() throws Exception {
        DossierService spyService = Mockito.spy(new DossierService(mailSender, documentGenerator));
        ReflectionTestUtils.setField(spyService, "sendDocumentUrlTemplate", "http://send/%s");

        InputStream brokenStream = mock(InputStream.class);
        when(brokenStream.readAllBytes()).thenThrow(new IOException("IO fail"));
        doReturn(brokenStream).when(spyService).getClassLoaderStream("templates/documentSend.html");

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ReflectionTestUtils.invokeMethod(spyService, "generateSendDocumentEmail", UUID.randomUUID())
        );
        assertTrue(ex.getMessage().contains("Ошибка при чтении HTML-шаблона"));
    }

    @Test
    void testGenerateSignDocumentEmail_throwsIOException() throws Exception {
        DossierService spyService = Mockito.spy(new DossierService(mailSender, documentGenerator));
        ReflectionTestUtils.setField(spyService, "signDocumentUrlTemplate", "http://sign/%s");

        InputStream brokenStream = mock(InputStream.class);
        when(brokenStream.readAllBytes()).thenThrow(new IOException("IO fail"));
        doReturn(brokenStream).when(spyService).getClassLoaderStream("templates/documentSign.html");

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ReflectionTestUtils.invokeMethod(spyService, "generateSignDocumentEmail", UUID.randomUUID())
        );
        assertTrue(ex.getMessage().contains("Ошибка при чтении HTML-шаблона"));
    }

    @Test
    void testGenerateCodeDocumentEmail_throwsIOException() throws Exception {
        DossierService spyService = Mockito.spy(new DossierService(mailSender, documentGenerator));
        ReflectionTestUtils.setField(spyService, "codeDocumentUrlTemplate", "http://code/%s");

        InputStream brokenStream = mock(InputStream.class);
        when(brokenStream.readAllBytes()).thenThrow(new IOException("IO fail"));
        doReturn(brokenStream).when(spyService).getClassLoaderStream("templates/documentCode.html");

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ReflectionTestUtils.invokeMethod(
                        spyService,
                        "generateCodeDocumentEmail",
                        UUID.randomUUID(),
                        UUID.randomUUID()
                )
        );
        assertTrue(ex.getMessage().contains("Ошибка при генерации содержимого email"));
    }

}
