package com.zephsie.wellbeing.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zephsie.wellbeing.utils.exceptions.*;
import com.zephsie.wellbeing.utils.responses.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Clock;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    private final Clock clock;

    @Autowired
    public GlobalControllerExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(ValidationException.class)
    private ResponseEntity<ErrorResponse> handleException(ValidationException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage(), clock.millis()));
    }

    @ExceptionHandler(JsonProcessingException.class)
    private ResponseEntity<ErrorResponse> handleException() {
        return ResponseEntity.badRequest().body(new ErrorResponse("Invalid JSON", clock.millis()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage(), clock.millis()));
    }

    @ExceptionHandler(WrongVersionException.class)
    public ResponseEntity<ErrorResponse> handleWrongVersionException(WrongVersionException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage(), clock.millis()));
    }

    @ExceptionHandler(NotUniqueException.class)
    public ResponseEntity<ErrorResponse> handleNotUniqueException(NotUniqueException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage(), clock.millis()));
    }

    @ExceptionHandler(IllegalPaginationValuesException.class)
    public ResponseEntity<ErrorResponse> handleIllegalPaginationValuesException(IllegalPaginationValuesException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage(), clock.millis()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ErrorResponse(e.getMessage(), clock.millis()));
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<ErrorResponse> handleEmailException(EmailException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage(), clock.millis()));
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialException(InvalidCredentialException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage(), clock.millis()));
    }
}