package com.kafka.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class LibraryEventControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleException(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors().stream().map(error ->
                        error.getField() + " - " + error.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(", "));

        log.info("MethodArgumentNotValidException caught in LibraryEventControllerAdvice: {}", errorMsg);
        return ResponseEntity.badRequest().body(errorMsg);
    }
}
