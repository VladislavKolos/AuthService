package com.example.emobile.exception.handler;

import com.example.emobile.exception.*;
import com.example.emobile.exception.builder.ValidationExceptionMessageBuilder;
import com.example.emobile.exception.dto.ExceptionDto;
import com.example.emobile.exception.enums.ErrorMessage;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
@RequiredArgsConstructor
public class AuthenticationServiceExceptionHandler {
    private final ValidationExceptionMessageBuilder validationExceptionMessageBuilder;

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(PasswordAuthenticationNotSupportedException.class)
    public ExceptionDto handlePasswordAuthenticationNotSupportedException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.PASSWORD_AUTH_NOT_SUPPORTED, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(JwtAuthenticationException.class)
    public ExceptionDto handleJwtAuthException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.JWT_AUTH_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ExceptionDto handleInvalidVerificationCodeException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.INVALID_VERIFICATION_CODE_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SecurityFilterConfigurationException.class)
    public ExceptionDto handleSecurityFilterConfigurationException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.SECURITY_FILTER_CONFIGURATION_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ExceptionDto handleUserNotFoundException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.USER_NOT_FOUND_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServletException.class)
    public ExceptionDto handleServletException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.SERVLET_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public ExceptionDto handleSQLException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.SQL_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ExceptionDto handleEmptyResultDataAccess(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.EMPTY_RESULT_DATA_ACCESS_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ExceptionDto handleMethodArgumentTypeMismatchException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.METHOD_ARGUMENT_TYPE_MISMATCH_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ExceptionDto handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        var result = ex.getBindingResult();

        List<FieldError> fieldErrors = result.getFieldErrors();

        if (!fieldErrors.isEmpty()) {
            String errorMessage = validationExceptionMessageBuilder.buildValidationErrorMessage(fieldErrors);

            return buildExceptionResponse(ErrorMessage.METHOD_ARGUMENT_NOT_VALID_ERROR, errorMessage,
                    getRequestPath(request));
        }
        return buildExceptionResponse(ErrorMessage.METHOD_ARGUMENT_NOT_VALID_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ExceptionDto handleConstraintViolationException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.CONSTRAINT_VIOLATION_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(IllegalStateException.class)
    public ExceptionDto handleIllegalStateException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.ILLEGAL_STATE_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public ExceptionDto handleNoSuchElementException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.NO_SUCH_ELEMENT_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NullPointerException.class)
    public ExceptionDto handleNullPointerException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.NULL_POINTER_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ExceptionDto handleIllegalArgumentException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.ILLEGAL_ARGUMENT_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public ExceptionDto handleNoResourceFoundException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.NO_RESOURCE_FOUND_ERROR, getRequestPath(request));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingPathVariableException.class)
    public ExceptionDto handleMissingPathVariableException(WebRequest request) {
        return buildExceptionResponse(ErrorMessage.MISSING_PATH_VARIABLE_ERROR, getRequestPath(request));
    }

    private String getRequestPath(WebRequest request) {
        return request.getDescription(false)
                .replace("uri=", "");
    }

    private ExceptionDto buildExceptionResponse(ErrorMessage errorMessage, String path) {
        return buildExceptionResponse(errorMessage, errorMessage.getMessage(), path);
    }

    private ExceptionDto buildExceptionResponse(ErrorMessage errorMessage, String customMessage, String path) {
        return ExceptionDto.builder()
                .errorCode(errorMessage.getErrorCode())
                .message(customMessage)
                .path(path)
                .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .build();
    }
}