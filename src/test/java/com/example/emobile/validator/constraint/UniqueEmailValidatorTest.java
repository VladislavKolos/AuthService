package com.example.emobile.validator.constraint;

import com.example.emobile.repository.UserRepository;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UniqueEmailValidatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @InjectMocks
    private UniqueEmailValidator uniqueEmailValidator;

    @Test
    public void shouldReturnTrueWhenEmailIsUnique() {
        String email = "newuser@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        boolean result = uniqueEmailValidator.isValid(email, constraintValidatorContext);

        assertThat(result).isTrue();
        verify(userRepository).existsByEmail(email);
    }

    @Test
    public void shouldReturnFalseWhenEmailExists() {
        String email = "existing@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean result = uniqueEmailValidator.isValid(email, constraintValidatorContext);

        assertThat(result).isFalse();
        verify(userRepository).existsByEmail(email);
    }
}