package com.example.emobile.validator.constraint.annotation;

import com.example.emobile.validator.constraint.UniqueEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
@Documented
public @interface UniqueEmail {
    String message() default "User with this email already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}