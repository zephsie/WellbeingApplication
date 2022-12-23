package com.zephsie.wellbeing.utils.exceptions;

public class WrongVersionException extends RuntimeException {
    public WrongVersionException() {
        super();
    }

    public WrongVersionException(String message) {
        super(message);
    }

    public WrongVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongVersionException(Throwable cause) {
        super(cause);
    }

    protected WrongVersionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}