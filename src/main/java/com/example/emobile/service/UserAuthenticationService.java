package com.example.emobile.service;

import com.example.emobile.dto.request.AuthenticationRequestDto;
import com.example.emobile.dto.request.VerificationCodeRequestDto;
import com.example.emobile.dto.response.AuthenticationResponseDto;
import com.example.emobile.dto.response.VerificationResponseDto;

public interface UserAuthenticationService {
    VerificationResponseDto initiateSignUp(AuthenticationRequestDto request);

    AuthenticationResponseDto confirmSignUp(String verificationToken, VerificationCodeRequestDto verificationCode);
}