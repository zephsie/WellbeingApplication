package com.zephsie.wellbeing.utils.converters;

import com.zephsie.wellbeing.utils.responses.MultipleErrorResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.HashMap;
import java.util.Map;

@Component
public class ErrorsToMultipleErrorResponseConverter {
    public MultipleErrorResponse map(Errors errors) {
        Map<String, String> messages = new HashMap<>();
        errors.getFieldErrors().forEach(error -> messages.put(error.getField(), error.getDefaultMessage()));
        return new MultipleErrorResponse("structured_error", messages);
    }
}