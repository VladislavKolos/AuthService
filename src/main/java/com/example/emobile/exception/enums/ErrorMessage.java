package com.example.emobile.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    SERVLET_ERROR(
            "Invalid request. Please check the URL and parameters.",
            400),

    METHOD_ARGUMENT_NOT_VALID_ERROR(
            "Some fields are incorrect or missing. Please check your input and try again.",
            400),

    METHOD_ARGUMENT_TYPE_MISMATCH_ERROR(
            "Incorrect data format. Please verify your input.",
            400),

    MISSING_PATH_VARIABLE_ERROR(
            "Required part of the request is missing. Please check the link.",
            400),

    ILLEGAL_ARGUMENT_ERROR(
            "Invalid data provided. Please check and try again.",
            400),

    CONSTRAINT_VIOLATION_ERROR(
            "Validation failed. Please review your input.",
            400),

    EMPTY_RESULT_DATA_ACCESS_ERROR(
            "Nothing found for your request.",
            404),

    NO_SUCH_ELEMENT_ERROR(
            "The requested item was not found.",
            404),

    NO_RESOURCE_FOUND_ERROR(
            "The requested resource could not be found.",
            404),

    PASSWORD_AUTH_NOT_SUPPORTED(
            "Password authentication is not supported in this version",
            501),

    USER_NOT_FOUND_ERROR(
            "We couldn't find a user with this email address.",
            404),

    INVALID_VERIFICATION_CODE_ERROR(
            "The verification code you entered is incorrect. Please try again.",
            401),

    JWT_AUTH_ERROR(
            "An error occurred during authentication. Please try again later.",
            500),

    SECURITY_FILTER_CONFIGURATION_ERROR(
            "A system error occurred during login. Please contact support.",
            500),

    SQL_ERROR(
            "A server error occurred while processing your request. Please try again later.",
            500),

    NULL_POINTER_ERROR(
            "An unexpected error occurred. Please try again later.",
            500),

    ILLEGAL_STATE_ERROR(
            "The operation cannot be completed at the moment. Please try again later.",
            403);

    private final String message;
    private final int errorCode;
}