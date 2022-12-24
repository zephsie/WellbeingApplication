package com.zephsie.wellbeing.controllers;

import com.zephsie.wellbeing.utils.responses.api.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.function.Function;

@ControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    private final Map<Class<? extends Exception>, Function<Exception, ResponseEntity<ErrorResponse>>> exceptionHandlers;
    private final Function<Exception, ResponseEntity<ErrorResponse>> defaultHandler;

    @Autowired
    public GlobalControllerExceptionHandler(Map<Class<? extends Exception>, Function<Exception, ResponseEntity<ErrorResponse>>> exceptionHandlers,
                                            Function<Exception, ResponseEntity<ErrorResponse>> defaultHandler) {

        this.exceptionHandlers = exceptionHandlers;
        this.defaultHandler = defaultHandler;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        Function<Exception, ResponseEntity<ErrorResponse>> handler = exceptionHandlers.get(exception.getClass());

        if (handler == null) {
            log.error("Unhandled exception", exception);
            handler = defaultHandler;
        }

        return handler.apply(exception);
    }
}