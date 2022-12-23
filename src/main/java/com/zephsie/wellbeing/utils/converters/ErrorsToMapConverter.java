package com.zephsie.wellbeing.utils.converters;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.HashMap;
import java.util.Map;

@Component
public class ErrorsToMapConverter {
    public Map<String, String> map(Errors errors) {
        Map<String, String> messages = new HashMap<>();
        errors.getFieldErrors().forEach(error -> messages.put(error.getField(), error.getDefaultMessage()));
        return messages;
    }
}