package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
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

    @Async
    public void sendAllNotifications(QuoteRequest quote) {
        this.sendNewLeadInternal(quote);
        this.sendCustomerConfirmationInternal(quote);
    }

    private void sendNewLeadInternal(QuoteRequest quote) {
        try {
            System.out.println("Sending owner lead email to: " + this.ownerEmail);

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
            System.out.println("Owner lead email sent successfully.");
        } catch (Exception e) {
            System.out.println("Error sending owner email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendCustomerConfirmationInternal(QuoteRequest quote) {
        if (quote.getEmail() == null || quote.getEmail().isBlank()) {
            System.out.println("Customer confirmation skipped because email was blank.");
            return;
        }

        try {
            System.out.println("Sending customer confirmation to: " + quote.getEmail());

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
            System.out.println("Customer confirmation email sent successfully.");
        } catch (Exception e) {
            System.out.println("Error sending customer confirmation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
