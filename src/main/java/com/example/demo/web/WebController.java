package com.example.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.EmailNotificationService;
import com.example.demo.GoogleSheetsWebhookService;
import com.example.demo.QuoteRequest;
import com.example.demo.SubmissionStorageService;

@Controller
public class WebController {

    private final SubmissionStorageService storage;
    private final EmailNotificationService email;
    private final GoogleSheetsWebhookService sheets;

    public WebController(SubmissionStorageService storage, EmailNotificationService email,
            GoogleSheetsWebhookService sheets) {
        this.storage = storage;
        this.email = email;
        this.sheets = sheets;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/services")
    public String services() {
        return "services";

    }

    @GetMapping("/gallery")
    public String gallery() {
        return "gallery";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("quote", new QuoteRequest());
        return "contact";
    }

    @PostMapping("/contact")
    public String submitContact(@ModelAttribute("quote") QuoteRequest quote,
            Model model) {
        this.storage.save(quote);
        this.email.sendAllNotifications(quote);
        this.sheets.send(quote);
        model.addAttribute("quote", quote);
        return "thanks";
    }

}
