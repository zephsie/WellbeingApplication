package com.zephsie.wellbeing.configs.exceptions;

import com.zephsie.wellbeing.utils.exceptions.*;
import com.zephsie.wellbeing.utils.responses.SingleErrorResponse;
import com.zephsie.wellbeing.utils.responses.api.ErrorResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;

import java.util.Map;
import java.util.function.Function;

@Configuration
public class ExceptionHandlingConfig {

    @Bean
    public Map<Class<? extends Exception>, Function<Exception, ResponseEntity<ErrorResponse>>> exceptionHandlers() {
        return Map.ofEntries(
                Map.entry(ValidationException.class, e ->
                        ResponseEntity.badRequest().body(new SingleErrorResponse("error", e.getMessage()))),
                Map.entry(HttpMessageNotReadableException.class, e ->
                        ResponseEntity.badRequest().body(new SingleErrorResponse("error", "Invalid JSON"))),
                Map.entry(NotFoundException.class, e ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(new SingleErrorResponse("error", e.getMessage()))),
                Map.entry(WrongVersionException.class, e ->
                        ResponseEntity.status(HttpStatus.CONFLICT).body(new SingleErrorResponse("error", e.getMessage()))),
                Map.entry(NotUniqueException.class, e ->
                        ResponseEntity.status(HttpStatus.CONFLICT).body(new SingleErrorResponse("error", e.getMessage()))),
                Map.entry(IllegalPaginationValuesException.class, e ->
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SingleErrorResponse("error", e.getMessage()))),
                Map.entry(InvalidCredentialException.class, e ->
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new SingleErrorResponse("error", e.getMessage()))),
                Map.entry(AccessDeniedException.class, e ->
                        ResponseEntity.status(HttpStatus.FORBIDDEN).body(new SingleErrorResponse("error", e.getMessage()))),
                Map.entry(BasicFieldValidationException.class, e ->
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(((BasicFieldValidationException) e).getMultipleErrorResponse()))
        );
    }

    @Bean
    public Function<Exception, ResponseEntity<ErrorResponse>> defaultHandler() {
        return e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new SingleErrorResponse("error", "Internal server error"));
    }
}