package com.example.emobile.exception;

public class SecurityFilterConfigurationException extends AuthenticationServiceException {
    public SecurityFilterConfigurationException(Throwable ex) {
        super("Error configuring security filter chain", ex);
    }
}