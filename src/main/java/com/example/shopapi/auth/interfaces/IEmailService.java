package com.example.shopapi.auth.interfaces;

public interface IEmailService {

    void send(
            String to,
            String subject,
            String text
    );
}