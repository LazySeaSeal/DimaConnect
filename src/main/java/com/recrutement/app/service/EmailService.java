package com.recrutement.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Confirm your email address");
        message.setText("Please confirm your email address by clicking the following link: "
                + "http://localhost:8082/api/auth/verify-email?email=" + to + "&token=" + token);

        mailSender.send(message);
    }
}