package com.example.emobile.util;

import com.example.emobile.dto.request.AuthenticationRequestDto;
import com.example.emobile.dto.request.VerificationCodeRequestDto;
import com.example.emobile.model.User;
import com.example.emobile.model.enums.UserRole;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.userdetails.UserDetails;

@UtilityClass
public class TestDataBuilderUtil {

    public static AuthenticationRequestDto validSignUpRequest() {
        return AuthenticationRequestDto.builder()
                .email("user@example.com")
                .build();
    }

    public static VerificationCodeRequestDto validVerificationCodeRequest() {
        return VerificationCodeRequestDto.builder()
                .code("123456")
                .build();
    }

    public static User validUser() {
        return User.builder()
                .email("user@example.com")
                .role(UserRole.ROLE_USER)
                .build();
    }

    public static UserDetails buildUserDetails(String email) {
        return User.builder()
                .email(email)
                .role(UserRole.ROLE_USER)
                .build();
    }
}