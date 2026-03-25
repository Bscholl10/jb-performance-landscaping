package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GoogleSheetsWebhookService {

    private final WebClient webClient;
    private final String webhookUrl;

    public GoogleSheetsWebhookService(
            @Value("${google.sheets.webhookUrl}") String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.webClient = WebClient.builder().build();
    }

    public void send(QuoteRequest quote) {
        try {
            this.webClient.post().uri(this.webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON).bodyValue(quote).retrieve()
                    .bodyToMono(String.class).block();
        } catch (Exception e) {
            // Do not break the website if Google is down
            System.out.println("Google Sheets webhook failed: " + e.getMessage());
        }
    }
}
