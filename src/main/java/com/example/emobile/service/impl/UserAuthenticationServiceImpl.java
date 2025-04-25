package com.example.emobile.service.impl;

import com.example.emobile.dto.request.AuthenticationRequestDto;
import com.example.emobile.dto.request.VerificationCodeRequestDto;
import com.example.emobile.dto.response.AuthenticationResponseDto;
import com.example.emobile.dto.response.VerificationResponseDto;
import com.example.emobile.exception.InvalidVerificationCodeException;
import com.example.emobile.model.User;
import com.example.emobile.model.enums.UserRole;
import com.example.emobile.repository.UserRepository;
import com.example.emobile.service.JwtService;
import com.example.emobile.service.UserAuthenticationService;
import com.example.emobile.service.VerificationCodeService;
import com.example.emobile.util.AuthenticationServiceConstantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAuthenticationServiceImpl implements UserAuthenticationService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final VerificationCodeService verificationCodeService;

    @Override
    public VerificationResponseDto initiateSignUp(AuthenticationRequestDto request) {
        verificationCodeService.generateAndSendCode(request.getEmail());

        String verificationToken = jwtService.generateVerificationToken(request.getEmail());

        return VerificationResponseDto.builder()
                .verificationToken(verificationToken)
                .build();
    }

    @Override
    @Transactional
    public AuthenticationResponseDto confirmSignUp(String verificationToken, VerificationCodeRequestDto request) {
        String email = jwtService.extractUsername(
                verificationToken.replace(AuthenticationServiceConstantUtil.BEARER_PREFIX, ""),
                jwtService.getVerificationSecretKey());

        if (!verificationCodeService.verifyCode(email, request.getCode())) {
            throw new InvalidVerificationCodeException();
        }

        var user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    var newUser = User.builder()
                            .email(email)
                            .role(UserRole.ROLE_USER)
                            .build();
                    return userRepository.save(newUser);
                });

        return AuthenticationResponseDto.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .build();
    }
}