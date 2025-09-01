package com.example.emobile.kafka;

import lombok.Builder;

@Builder
public record VerificationCodeEvent(String email, String code) {
}