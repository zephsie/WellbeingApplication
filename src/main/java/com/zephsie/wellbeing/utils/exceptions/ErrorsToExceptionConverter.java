package com.zephsie.wellbeing.utils.exceptions;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.StringJoiner;

@Component
public class ErrorsToExceptionConverter {
    public <T extends CustomException> T mapErrorsToException(Errors errors, Class<T> exceptionClass) {
        StringJoiner stringJoiner = new StringJoiner(", ");
        errors.getAllErrors().forEach(error -> stringJoiner.add(error.getDefaultMessage()));
        try {
            return exceptionClass.getConstructor(String.class).newInstance(stringJoiner.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
