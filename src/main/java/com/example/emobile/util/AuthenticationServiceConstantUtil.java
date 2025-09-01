package com.example.emobile.util;

import lombok.experimental.UtilityClass;

import java.time.Duration;

@UtilityClass
public class AuthenticationServiceConstantUtil {
    public static final int MIN_RANDOM_VALUE = 0;
    public static final int MAX_RANDOM_VALUE = 10;
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();
    public static final Duration ACCESS_TOKEN_EXPIRATION = Duration.ofMinutes(30);
    public static final Duration VERIFICATION_TOKEN_EXPIRATION = Duration.ofMinutes(1);
}