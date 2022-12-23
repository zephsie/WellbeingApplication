package com.zephsie.wellbeing.utils.exceptions;

import java.util.Map;

public class BasicFieldValidationException extends RuntimeException {
    private final Map<String, String> errors;

    public BasicFieldValidationException(Map<String, String> errors) {
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
