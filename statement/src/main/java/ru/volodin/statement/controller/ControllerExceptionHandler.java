package ru.volodin.statement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.volodin.errorhandling_lib.dto.ErrorMessageDto;
import ru.volodin.errorhandling_lib.exception.OffersException;
import ru.volodin.errorhandling_lib.exception.ScoringException;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler extends ru.volodin.errorhandling_lib.exception.ControllerExceptionHandler {

    public ControllerExceptionHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @ExceptionHandler(ScoringException.class)
    public ResponseEntity<ErrorMessageDto> handleScoringException(ScoringException e, WebRequest request) {
        ErrorMessageDto nested = null;

        String raw = e.getRawRemoteError();
        try {
            if (raw != null) {
                if (raw.startsWith("\"{") && raw.endsWith("}\"")) {
                    String unquoted = objectMapper.readValue(raw, String.class);
                    nested = objectMapper.readValue(unquoted, ErrorMessageDto.class);
                } else if (raw.startsWith("{")) {
                    nested = objectMapper.readValue(raw, ErrorMessageDto.class);
                }
            }
        } catch (Exception ex) {
            log.warn("Failed to deserialize nested ErrorMessageDto from ScoringException", ex);
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(
                        e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        request,
                        nested
                ));
    }

    @ExceptionHandler(OffersException.class)
    public ResponseEntity<ErrorMessageDto> handleOffersException(OffersException e, WebRequest request) {
        ErrorMessageDto nested = null;
        try {
            String raw = e.getRawRemoteError();
            if (raw != null) {
                if (raw.startsWith("\"{") && raw.endsWith("}\"")) {
                    String unquoted = objectMapper.readValue(raw, String.class);
                    nested = objectMapper.readValue(unquoted, ErrorMessageDto.class);
                } else if (raw.startsWith("{")) {
                    nested = objectMapper.readValue(raw, ErrorMessageDto.class);
                }
            }
        } catch (Exception ex) {
            log.warn("Failed to deserialize nested ErrorMessageDto from OffersException", ex);
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(
                        e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        request,
                        nested
                ));
    }

}
