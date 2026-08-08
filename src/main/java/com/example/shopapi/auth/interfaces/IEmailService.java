package com.example.shopapi.auth.interfaces;

public interface IEmailService {

    void sendVerificationEmail(
            String email,
            String token
    );

    void sendPasswordResetEmail(
            String email,
            String token
    );
}