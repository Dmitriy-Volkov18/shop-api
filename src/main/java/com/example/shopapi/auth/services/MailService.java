package com.example.shopapi.auth.services;

import com.example.shopapi.auth.interfaces.IEmailService;
import com.example.shopapi.common.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService implements IEmailService {

    private final JavaMailSender mailSender;
    private final AppProperties appProperties;

    public void send(
            String to,
            String subject,
            String text
    ) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(appProperties.getMailFrom());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);

        log.warn("Email is successfully sent");
    }
}