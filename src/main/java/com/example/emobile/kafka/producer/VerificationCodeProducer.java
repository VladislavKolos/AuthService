package com.example.emobile.kafka.producer;

import com.example.emobile.kafka.VerificationCodeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationCodeProducer {
    private final KafkaTemplate<String, VerificationCodeEvent> kafkaTemplate;

    @Value("${kafka.topics.verification-code}")
    private String verificationCodeTopic;

    public void sendVerificationCode(String email, String code) {
        var message = VerificationCodeEvent.builder()
                .email(email)
                .code(code)
                .build();

        kafkaTemplate.send(verificationCodeTopic, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send verification code for email: {}", email, ex);
                    } else {
                        log.info("Verification code sent to Kafka for email: {}", email);
                    }
                });
    }
}