package com.zephsie.wellbeing.utils.responses;

import com.zephsie.wellbeing.utils.responses.api.ErrorResponse;

public class SingleErrorResponse extends ErrorResponse {
    private String message;

    public SingleErrorResponse(String logref, String message) {
        super(logref);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}