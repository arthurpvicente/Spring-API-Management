package com.arthurpv15.apimanagement.whatsapp;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Body;
import com.twilio.twiml.messaging.Message;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/webhook")
@Tag(name = "WhatsApp", description = "Twilio WhatsApp webhook")
public class WhatsAppWebhookController {

    private final WhatsAppBotService botService;

    public WhatsAppWebhookController(WhatsAppBotService botService) {
        this.botService = botService;
    }

    @PostMapping(value = "/whatsapp", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "Handle incoming WhatsApp messages from Twilio")
    public String handleIncomingMessage(
            @RequestParam("From") String from,
            @RequestParam("Body") String body) {

        String reply = botService.processMessage(from, body);

        MessagingResponse response = new MessagingResponse.Builder()
                .message(new Message.Builder()
                        .body(new Body.Builder(reply).build())
                        .build())
                .build();

        return response.toXml();
    }
}
