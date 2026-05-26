package com.arthurpv15.apimanagement.whatsapp;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.arthurpv15.apimanagement.config.TwilioConfig;
import com.twilio.security.RequestValidator;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TwilioSignatureFilter extends OncePerRequestFilter {

    private final TwilioConfig twilioConfig;
    private final boolean validationEnabled;

    public TwilioSignatureFilter(TwilioConfig twilioConfig,
                                 @Value("${twilio.signature.validation.enabled:false}") boolean validationEnabled) {
        this.twilioConfig = twilioConfig;
        this.validationEnabled = validationEnabled;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/webhook/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!validationEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String signature = request.getHeader("X-Twilio-Signature");
        if (signature == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Missing Twilio signature");
            return;
        }

        String url = request.getRequestURL().toString();
        Map<String, String> params = Collections.list(request.getParameterNames()).stream()
                .collect(Collectors.toMap(name -> name, request::getParameter));

        RequestValidator validator = new RequestValidator(twilioConfig.getAuthToken());
        if (!validator.validate(url, params, signature)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid Twilio signature");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
