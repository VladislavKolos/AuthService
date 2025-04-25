package com.example.emobile.service.impl;

import com.example.emobile.kafka.producer.VerificationCodeProducer;
import com.example.emobile.service.VerificationCodeService;
import com.example.emobile.util.AuthenticationServiceConstantUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {
    private final SecureRandom secureRandom;
    private final StringRedisTemplate redisTemplate;
    private final VerificationCodeProducer verificationCodeProducer;

    @Value("${verification.code.length}")
    private int codeLength;

    @Value("${verification.code.expiration}")
    private long codeExpirationMs;

    @Value("${verification.key.prefix}")
    private String redisKeyPrefix;

    @Override
    public void generateAndSendCode(String email) {
        String code = generateSecureCode();
        storeCode(email, code);
        verificationCodeProducer.sendVerificationCode(email, code);
    }

    @Override
    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(buildRedisKey(email));
        return code.equals(storedCode);
    }

    private String generateSecureCode() {
        return secureRandom.ints(AuthenticationServiceConstantUtil.MIN_RANDOM_VALUE, AuthenticationServiceConstantUtil.MAX_RANDOM_VALUE)
                .limit(codeLength)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining());
    }

    private void storeCode(String email, String code) {
        redisTemplate.opsForValue().set(
                buildRedisKey(email),
                code,
                Duration.ofMillis(codeExpirationMs)
        );
    }

    private String buildRedisKey(String email) {
        return redisKeyPrefix + ":" + email;
    }
}