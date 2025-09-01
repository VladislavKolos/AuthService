package com.example.emobile.service.impl;

import com.example.emobile.exception.InvalidVerificationCodeException;
import com.example.emobile.model.enums.UserRole;
import com.example.emobile.repository.UserRepository;
import com.example.emobile.service.JwtService;
import com.example.emobile.service.VerificationCodeService;
import com.example.emobile.util.TestDataBuilderUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class UserAuthenticationServiceImplTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationCodeService verificationCodeService;

    @InjectMocks
    private UserAuthenticationServiceImpl userAuthenticationService;

    @Test
    public void shouldInitiateSignUpSuccessfully() {
        var request = TestDataBuilderUtil.validSignUpRequest();
        String expectedToken = "jwt-token";
        given(jwtService.generateVerificationToken(request.getEmail()))
                .willReturn(expectedToken);

        var response = userAuthenticationService.initiateSignUp(request);

        then(verificationCodeService).should().generateAndSendCode(request.getEmail());
        then(jwtService).should().generateVerificationToken(request.getEmail());
        assertThat(response.verificationToken()).isEqualTo(expectedToken);
    }

    @Test
    public void shouldConfirmSignUpWithExistingUser() {
        String token = "Bearer jwt-token";
        String email = "user@example.com";
        String secretKey = "verification-secret";
        var codeRequest = TestDataBuilderUtil.validVerificationCodeRequest();
        var existingUser = TestDataBuilderUtil.validUser();
        String expectedAccessToken = "access-token";

        given(jwtService.getVerificationSecretKey())
                .willReturn(secretKey);
        given(jwtService.extractUsername("jwt-token", jwtService.getVerificationSecretKey()))
                .willReturn(email);
        given(verificationCodeService.verifyCode(email, codeRequest.getCode()))
                .willReturn(true);
        given(userRepository.findByEmail(email))
                .willReturn(Optional.of(existingUser));
        given(jwtService.generateAccessToken(existingUser))
                .willReturn(expectedAccessToken);

        var response = userAuthenticationService.confirmSignUp(token, codeRequest);

        then(jwtService).should().extractUsername("jwt-token", secretKey);
        then(verificationCodeService).should().verifyCode(email, codeRequest.getCode());
        then(userRepository).should().findByEmail(email);
        then(jwtService).should().generateAccessToken(existingUser);

        assertThat(response.accessToken()).isEqualTo(expectedAccessToken);
    }

    @Test
    public void shouldConfirmSignUpAndCreateNewUser() {
        String token = "Bearer jwt-token";
        String email = "newuser@example.com";
        var codeRequest = TestDataBuilderUtil.validVerificationCodeRequest();
        var newUser = TestDataBuilderUtil.validUser();
        newUser.setEmail(email);
        String expectedAccessToken = "access-token";

        given(jwtService.extractUsername("jwt-token", jwtService.getVerificationSecretKey()))
                .willReturn(email);
        given(verificationCodeService.verifyCode(email, codeRequest.getCode()))
                .willReturn(true);
        given(userRepository.findByEmail(email))
                .willReturn(Optional.empty());
        given(userRepository.save(newUser))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(jwtService.generateAccessToken(newUser))
                .willReturn(expectedAccessToken);

        var response = userAuthenticationService.confirmSignUp(token, codeRequest);

        then(userRepository).should().save(argThat(user ->
                user.getEmail().equals(email) && user.getRole() == UserRole.ROLE_USER));
        then(jwtService).should().generateAccessToken(newUser);
        assertThat(response.accessToken()).isEqualTo(expectedAccessToken);
    }

    @Test
    public void shouldThrowExceptionWhenVerificationCodeInvalid() {
        String token = "Bearer jwt-token";
        String email = "user@example.com";
        var codeRequest = TestDataBuilderUtil.validVerificationCodeRequest();

        given(jwtService.extractUsername("jwt-token", jwtService.getVerificationSecretKey()))
                .willReturn(email);
        given(verificationCodeService.verifyCode(email, codeRequest.getCode()))
                .willReturn(false);

        assertThatThrownBy(() -> userAuthenticationService.confirmSignUp(token, codeRequest))
                .isInstanceOf(InvalidVerificationCodeException.class);
    }
}