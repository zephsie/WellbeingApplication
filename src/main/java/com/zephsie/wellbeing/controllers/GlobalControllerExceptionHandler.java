package com.zephsie.wellbeing.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zephsie.wellbeing.utils.exceptions.*;
import com.zephsie.wellbeing.utils.responses.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    private ResponseEntity<ErrorResponse> handleException(ValidationException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(JsonProcessingException.class)
    private ResponseEntity<ErrorResponse> handleException() {
        return ResponseEntity.badRequest().body(new ErrorResponse("Invalid JSON", System.currentTimeMillis()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(WrongVersionException.class)
    public ResponseEntity<ErrorResponse> handleWrongVersionException(WrongVersionException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(NotUniqueException.class)
    public ResponseEntity<ErrorResponse> handleNotUniqueException(NotUniqueException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(IllegalPaginationValuesException.class)
    public ResponseEntity<ErrorResponse> handleIllegalPaginationValuesException(IllegalPaginationValuesException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<ErrorResponse> handleEmailException(EmailException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialException(InvalidCredentialException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage(), System.currentTimeMillis()));
    }
}