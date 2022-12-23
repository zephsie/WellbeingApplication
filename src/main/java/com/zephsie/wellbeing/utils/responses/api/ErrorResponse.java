package com.zephsie.wellbeing.utils.responses.api;

public abstract class ErrorResponse {
    private String logref;

    public ErrorResponse(String logref) {
        this.logref = logref;
    }

    public String getLogref() {
        return logref;
    }

    public void setLogref(String logref) {
        this.logref = logref;
    }
}
