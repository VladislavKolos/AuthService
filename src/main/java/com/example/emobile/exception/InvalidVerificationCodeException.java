package com.example.emobile.exception;

public class InvalidVerificationCodeException extends AuthenticationServiceException {
    public InvalidVerificationCodeException() {
        super("Invalid verification code");
    }
}