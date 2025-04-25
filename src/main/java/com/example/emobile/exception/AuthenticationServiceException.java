package com.example.emobile.exception;

public abstract class AuthenticationServiceException extends RuntimeException {
    public AuthenticationServiceException(String message) {
        super(message);
    }

    public AuthenticationServiceException(String message, Throwable ex) {
        super(message, ex);
    }
}