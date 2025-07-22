package ru.volodin.dossier.service.provider;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.volodin.dossier.exceptions.DocumentGenerationException;
import ru.volodin.dossier.kafka.dto.EmailMessageWithCreditDto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentGenerator {
    private final SpringTemplateEngine engine;

    public DataSource generateDocument(EmailMessageWithCreditDto emailMessage) {
        log.info("Starting document generation for EmailMessageWithCreditDto: {}", emailMessage);

        Context context = new Context();
        context.setVariables(Map.of("message", emailMessage));
        String content = engine.process("credit-document", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            writePdf(outputStream, content);
            log.info("Document generation completed successfully for EmailMessageWithCreditDto: {}", emailMessage);
            return new ByteArrayDataSource(outputStream.toByteArray(), "application/pdf");
        } catch (IOException e) {
            log.error("Error writing PDF for EmailMessageWithCreditDto: {}", emailMessage, e);
            throw new DocumentGenerationException("Failed to generate PDF document.");
        }
    }

    private void writePdf(ByteArrayOutputStream outputStream, String content) {
        log.debug("Starting PDF conversion for content: {}", content);

        try {
            PdfWriter writer = new PdfWriter(outputStream);
            Document document = HtmlConverter.convertToDocument(content, writer);
            document.close();
            log.debug("PDF conversion completed successfully.");
        } catch (Exception e) {
            log.error("Error occurred during HTML to PDF conversion.", e);
            throw new DocumentGenerationException("Error occurred while converting HTML to PDF.");
        }
    }
}