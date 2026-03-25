package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.owner.email}")
    private String ownerEmail;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNewLead(QuoteRequest quote) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.fromEmail);
        message.setTo(this.ownerEmail);
        message.setSubject("New Website Lead: " + this.safe(quote.getService()));

        message.setText("You received a new website quote request.\n\n" + "Name: "
                + this.safe(quote.getName()) + "\n" + "Email: "
                + this.safe(quote.getEmail()) + "\n" + "Phone: "
                + this.safe(quote.getPhone()) + "\n" + "Service: "
                + this.safe(quote.getService()) + "\n\n" + "Message:\n"
                + this.safe(quote.getMessage()));

        this.mailSender.send(message);
    }

    public void sendCustomerConfirmation(QuoteRequest quote) {
        if (quote.getEmail() == null || quote.getEmail().isBlank()) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.fromEmail);
        message.setTo(quote.getEmail());
        message.setSubject("We got your request - JB Performance Landscaping");

        message.setText("Hi " + this.safe(quote.getName()) + ",\n\n"
                + "Thanks for reaching out to JB Performance Landscaping. "
                + "We received your request and will follow up as soon as possible.\n\n"
                + "Here is a copy of your submission:\n\n" + "Service: "
                + this.safe(quote.getService()) + "\n" + "Phone: "
                + this.safe(quote.getPhone()) + "\n" + "Message: "
                + this.safe(quote.getMessage()) + "\n\n"
                + "We appreciate your interest and look forward to helping you out.\n\n"
                + "JB Performance Landscaping");

        this.mailSender.send(message);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
