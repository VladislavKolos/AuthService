package com.example.emobile.controller;

import com.example.emobile.dto.request.AuthenticationRequestDto;
import com.example.emobile.dto.request.VerificationCodeRequestDto;
import com.example.emobile.dto.response.AuthenticationResponseDto;
import com.example.emobile.dto.response.VerificationResponseDto;
import com.example.emobile.service.UserAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserAuthenticationService userAuthService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public VerificationResponseDto signUp(@Valid @RequestBody AuthenticationRequestDto request) {
        return userAuthService.initiateSignUp(request);
    }

    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponseDto verifyCode(@RequestHeader("Verification") String verificationToken,
                                                @Valid @RequestBody VerificationCodeRequestDto request) {
        return userAuthService.confirmSignUp(verificationToken, request);
    }
}