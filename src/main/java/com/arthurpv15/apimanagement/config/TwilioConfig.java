package com.arthurpv15.apimanagement.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @PostConstruct
    public void init() {
        if (!"placeholder".equals(accountSid)) {
            Twilio.init(accountSid, authToken);
        }
    }

    public String getAuthToken() {
        return authToken;
    }
}
