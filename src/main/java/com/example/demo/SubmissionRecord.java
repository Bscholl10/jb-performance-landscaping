package com.example.demo;

public class SubmissionRecord {
    public final String timestamp;
    public final String name;
    public final String email;
    public final String phone;
    public final String service;
    public final String message;

    public SubmissionRecord(String timestamp, String name, String email, String phone,
            String service, String message) {
        this.timestamp = timestamp;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.service = service;
        this.message = message;
    }
}
