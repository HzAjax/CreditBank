package ru.volodin.CreditBank.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.volodin.CreditBank.entity.dto.ErrorMessageDto;
import ru.volodin.CreditBank.exeptions.ScoringException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageDto> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.debug("MethodArgumentNotValidException occurred: {}",message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessageDto(message));
    }

    @ExceptionHandler(ScoringException.class)
    public ResponseEntity<ErrorMessageDto> handlerScoring(ScoringException e) {
        String message = e.getMessage();
        log.debug("ScoringException occurred: {}", message);

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorMessageDto(message));
    }
}
