package com.zephsie.wellbeing.configs.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zephsie.wellbeing.utils.exceptions.*;
import com.zephsie.wellbeing.utils.responses.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Clock;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class ExceptionHandlingConfig {

    private final Clock clock;

    @Autowired
    public ExceptionHandlingConfig(Clock clock) {
        this.clock = clock;
    }

    @Bean
    public Map<Class<? extends Exception>, Function<Exception, ResponseEntity<ErrorResponse>>> exceptionHandlers() {
        return Map.ofEntries(
                Map.entry(ValidationException.class, e ->
                        ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), clock.millis()))),
                Map.entry(JsonProcessingException.class, e ->
                        ResponseEntity.badRequest().body(new ErrorResponse("Invalid JSON", clock.millis()))),
                Map.entry(NotFoundException.class, e ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage(), clock.millis()))),
                Map.entry(WrongVersionException.class, e ->
                        ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage(), clock.millis()))),
                Map.entry(NotUniqueException.class, e ->
                        ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage(), clock.millis()))),
                Map.entry(IllegalPaginationValuesException.class, e ->
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage(), clock.millis()))),
                Map.entry(InvalidCredentialException.class, e ->
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage(), clock.millis())))
        );
    }

    @Bean
    public Function<Exception, ResponseEntity<ErrorResponse>> defaultHandler() {
        return e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred", clock.millis()));
    }
}