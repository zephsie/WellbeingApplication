package com.zephsie.wellbeing.utils.exceptions;

import com.zephsie.wellbeing.utils.responses.MultipleErrorResponse;
public class BasicFieldValidationException extends RuntimeException {
    private final MultipleErrorResponse multipleErrorResponse;

    public BasicFieldValidationException(MultipleErrorResponse multipleErrorResponse) {
        this.multipleErrorResponse = multipleErrorResponse;
    }

    public MultipleErrorResponse getMultipleErrorResponse() {
        return multipleErrorResponse;
    }
}
