package ru.volodin.calculator.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import ru.volodin.calculator.entity.dto.api.response.ErrorMessageDto;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageDto> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e
                                                                                    ,HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.debug("MethodArgumentNotValidException occurred: {}",message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDto(message, HttpStatus.BAD_REQUEST.value(), request.getRequestURI(),LocalDateTime.now()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessageDto> handleHttpMessageNotReadable(HttpMessageNotReadableException e,
                                                                        HttpServletRequest request) {
        String message = "Invalid request body: " + e.getMostSpecificCause().getMessage();
        log.warn("Bad JSON: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDto(message, HttpStatus.BAD_REQUEST.value(), request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessageDto> handleMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                                    HttpServletRequest request) {
        String message = "Method not allowed: " + e.getMethod();
        log.warn("Wrong method: {}", message);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorMessageDto(message, HttpStatus.METHOD_NOT_ALLOWED.value(), request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorMessageDto> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e,
                                                                       HttpServletRequest request) {
        String message = "Unsupported media type: " + e.getContentType();
        log.warn("Media type error: {}", message);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new ErrorMessageDto(message, HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorMessageDto> handleMissingParameter(MissingServletRequestParameterException e,
                                                                  HttpServletRequest request) {
        String message = "Missing request parameter: " + e.getParameterName();
        log.warn("Missing param: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDto(message, HttpStatus.BAD_REQUEST.value(), request.getRequestURI(), LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDto> handleUnexpected(Exception e, HttpServletRequest request) {
        log.error("Unexpected error: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageDto("Unexpected error: " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        request.getRequestURI(),
                        LocalDateTime.now()));
    }
}
