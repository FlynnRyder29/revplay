package com.revplay.exception;
public class RevPlayException extends Exception {
    public RevPlayException(String message) {
        super(message);
    }

    public RevPlayException(String message, Throwable cause) {
        super(message, cause);
    }
}