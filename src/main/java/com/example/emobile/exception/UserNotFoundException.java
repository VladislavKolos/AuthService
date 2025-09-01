package com.example.emobile.exception;

public class UserNotFoundException extends AuthenticationServiceException {
    public UserNotFoundException(String email) {
        super("User with this email: " + email + " was not found in database");
    }
}