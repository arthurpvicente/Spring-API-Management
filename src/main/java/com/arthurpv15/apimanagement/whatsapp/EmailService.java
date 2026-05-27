package com.arthurpv15.apimanagement.whatsapp;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;

@Service
public class EmailService {

    private final String apiKey;
    private final boolean enabled;
    private final String from;

    public EmailService(
            @Value("${resend.api.key:disabled}") String apiKey,
            @Value("${mail.enabled:true}") boolean enabled,
            @Value("${mail.from:onboarding@resend.dev}") String from) {
        this.apiKey = apiKey;
        this.enabled = enabled;
        this.from = from;
    }

    public void sendVerificationCode(String toEmail, String code) {
        if (!enabled || "disabled".equals(apiKey)) {
            LoggerFactory.getLogger(EmailService.class)
                    .info("Verification code for {}: {}", toEmail, code);
            return;
        }

        Resend resend = new Resend(apiKey);
        CreateEmailOptions request = CreateEmailOptions.builder()
                .from(from)
                .to(List.of(toEmail))
                .subject("Your verification code")
                .text("Your verification code is: " + code + "\n\nIt expires in 5 minutes.")
                .build();

        try {
            resend.emails().send(request);
        } catch (Exception e) {
            LoggerFactory.getLogger(EmailService.class)
                    .error("Failed to send verification email to {}: {}", toEmail, e.getMessage());
        }
    }
}
