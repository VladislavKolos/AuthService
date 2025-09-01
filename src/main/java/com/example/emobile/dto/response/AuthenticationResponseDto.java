package com.example.emobile.dto.response;

import lombok.Builder;

@Builder
public record AuthenticationResponseDto(String accessToken) {
}