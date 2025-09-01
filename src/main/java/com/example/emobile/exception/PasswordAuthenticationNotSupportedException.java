package com.example.emobile.exception;

public class PasswordAuthenticationNotSupportedException extends AuthenticationServiceException {
    public PasswordAuthenticationNotSupportedException() {
        super("Password-based authentication is not supported. This application uses alternative authentication methods.");
    }
}