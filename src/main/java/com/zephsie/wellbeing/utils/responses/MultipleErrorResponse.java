package com.zephsie.wellbeing.utils.responses;

import com.zephsie.wellbeing.utils.responses.api.ErrorResponse;

import java.util.Map;

public class MultipleErrorResponse extends ErrorResponse {
    private Map<String, String> errors;

    public MultipleErrorResponse(String logref, Map<String, String> errors) {
        super(logref);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}