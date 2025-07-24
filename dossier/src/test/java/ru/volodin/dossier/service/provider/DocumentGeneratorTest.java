package ru.volodin.dossier.service.provider;

import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.volodin.dossier.exceptions.DocumentGenerationException;
import ru.volodin.dossier.kafka.dto.EmailMessageWithCreditDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DocumentGeneratorTest {

    private SpringTemplateEngine engine;
    private DocumentGenerator generator;

    @BeforeEach
    void setUp() {
        engine = mock(SpringTemplateEngine.class);
        generator = new DocumentGenerator(engine);
    }

    @Test
    void testGenerateDocument_success() throws IOException {
        EmailMessageWithCreditDto dto = new EmailMessageWithCreditDto();
        when(engine.process(eq("credit-document"), any())).thenReturn("<html><body><p>Hello</p></body></html>");

        DataSource result = generator.generateDocument(dto);

        assertNotNull(result);
        assertThat(result.getContentType()).isEqualTo("application/pdf");
        assertThat(result.getInputStream().available()).isGreaterThan(0);
    }

    @Test
    void testGenerateDocument_invalidHtml_throwsDocumentGenerationException() {
        EmailMessageWithCreditDto dto = new EmailMessageWithCreditDto();
        when(engine.process(eq("credit-document"), any())).thenReturn("<html><invalid></body>");

        DocumentGenerationException ex = assertThrows(DocumentGenerationException.class,
                () -> generator.generateDocument(dto));

        assertEquals("Error occurred while converting HTML to PDF.", ex.getMessage());
        assertNotNull(ex.getCause());
    }

    @Test
    void testGenerateDocument_engineThrowsException_wrappedInDocumentGenerationException() {
        EmailMessageWithCreditDto dto = new EmailMessageWithCreditDto();
        when(engine.process(eq("credit-document"), any()))
                .thenThrow(new RuntimeException("Thymeleaf error"));

        DocumentGenerationException ex = assertThrows(DocumentGenerationException.class,
                () -> generator.generateDocument(dto));

        assertEquals("Failed to process HTML template", ex.getMessage());
        assertNotNull(ex.getCause());
        assertEquals("Thymeleaf error", ex.getCause().getMessage());
    }

    @Test
    void testGenerateDocument_dataSourceCreationThrowsIOException() throws IOException {
        EmailMessageWithCreditDto dto = new EmailMessageWithCreditDto();

        SpringTemplateEngine engine = mock(SpringTemplateEngine.class);
        when(engine.process(eq("credit-document"), any())).thenReturn("<html><body><p>Hello</p></body></html>");

        DocumentGenerator generator = new DocumentGenerator(engine) {
            @Override
            protected ByteArrayDataSource createDataSource(byte[] data) throws IOException {
                throw new IOException("Simulated IO error");
            }
        };

        DocumentGenerationException ex = assertThrows(DocumentGenerationException.class,
                () -> generator.generateDocument(dto));

        assertEquals("Failed to generate PDF document.", ex.getMessage());
        assertNotNull(ex.getCause());
        assertEquals("Simulated IO error", ex.getCause().getMessage());
    }
}
