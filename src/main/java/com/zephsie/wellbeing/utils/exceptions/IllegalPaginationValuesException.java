package com.zephsie.wellbeing.utils.exceptions;

public class IllegalPaginationValuesException extends RuntimeException {
    public IllegalPaginationValuesException() {
        super();
    }

    public IllegalPaginationValuesException(String message) {
        super(message);
    }

    public IllegalPaginationValuesException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalPaginationValuesException(Throwable cause) {
        super(cause);
    }

    protected IllegalPaginationValuesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}