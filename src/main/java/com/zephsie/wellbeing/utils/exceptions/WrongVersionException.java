package com.zephsie.wellbeing.utils.exceptions;

public class WrongVersionException extends RuntimeException {
    public WrongVersionException(String message) {
        super(message);
    }
}