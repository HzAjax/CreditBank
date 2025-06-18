package ru.volodin.calculator.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

}
