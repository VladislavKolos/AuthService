package com.example.emobile.service;

public interface VerificationCodeService {
    void generateAndSendCode(String email);

    boolean verifyCode(String email, String code);
}