package ru.volodin.deal.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.volodin.deal.entity.dto.api.ErrorMessageDto;
import ru.volodin.deal.exceptions.ScoringException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessageDto> handlerStatementNotFoundException(Exception e) {
        log.error("Error = {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessageDto(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageDto> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.error("Error = {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDto(message));
    }

    @ExceptionHandler(ScoringException.class)
    public ResponseEntity<ErrorMessageDto> handlerScoringException(ScoringException e) {
        String message = e.getMessage();
        log.debug("ScoringException occurred: {}", message);

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorMessageDto(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessageDto> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        String message = e.getMostSpecificCause().getMessage();
        log.warn("Malformed JSON or unreadable body: {}", message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDto("Invalid request body: " + message));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessageDto> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        String message = "HTTP method not supported: " + e.getMethod();
        log.warn(message);
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorMessageDto(message));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorMessageDto> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        String message = "Unsupported media type: " + e.getContentType();
        log.warn(message);
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new ErrorMessageDto(message));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorMessageDto> handleMissingParameter(MissingServletRequestParameterException e) {
        String message = "Missing request parameter: " + e.getParameterName();
        log.warn(message);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDto(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDto> handleAllUncaughtException(Exception e) {
        log.error("Unexpected error occurred: ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageDto("Internal server error: " + e.getMessage()));
    }

}

