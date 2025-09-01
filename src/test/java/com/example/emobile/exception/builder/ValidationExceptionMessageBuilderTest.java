package com.example.emobile.exception.builder;

import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ValidationExceptionMessageBuilderTest {
    private final ValidationExceptionMessageBuilder builder = new ValidationExceptionMessageBuilder();

    @Test
    public void shouldBuildSingleFieldErrorMessage() {
        List<FieldError> fieldErrors = Collections.singletonList(
                createFieldError("email", "must not be blank"));

        String result = builder.buildValidationErrorMessage(fieldErrors);

        assertThat(result).isEqualTo("Validation failed for fields: email (must not be blank)");
    }

    @Test
    public void shouldBuildMultipleFieldErrorMessage() {
        List<FieldError> fieldErrors = List.of(
                createFieldError("email", "must not be blank"),
                createFieldError("password", "must be at least 8 characters"));

        String result = builder.buildValidationErrorMessage(fieldErrors);

        assertThat(result).isEqualTo(
                "Validation failed for fields: email (must not be blank), password (must be at least 8 characters)");
    }

    @Test
    public void shouldReturnBaseMessageForEmptyList() {
        List<FieldError> fieldErrors = Collections.emptyList();

        String result = builder.buildValidationErrorMessage(fieldErrors);

        assertThat(result).isEqualTo("Validation failed for fields: ");
    }

    private FieldError createFieldError(String field, String message) {
        return new FieldError("objectName", field, message);
    }
}