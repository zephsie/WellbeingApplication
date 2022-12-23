package com.zephsie.wellbeing.utils.exceptions;

public class NotUniqueException extends RuntimeException {
    public NotUniqueException() {
        super();
    }

    public NotUniqueException(String message) {
        super(message);
    }

    public NotUniqueException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotUniqueException(Throwable cause) {
        super(cause);
    }

    protected NotUniqueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}