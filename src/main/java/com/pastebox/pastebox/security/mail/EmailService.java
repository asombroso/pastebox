package com.pastebox.pastebox.security.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class EmailService {

    private final JavaMailSender emailSender;

    @Value("${app.host}")
    private String host;

    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendMessage(String code, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("example@example.com");
        message.setTo(email);
        message.setSubject("Pastebox registration confirmation");
        message.setText("Please click the link to confirm your email: " + host + "/confirm?code=" + code);
        try {
            emailSender.send(message);
        } catch (MailException ex) {
            ex.printStackTrace();
        }
    }

    public String generateCode(){
        return UUID.randomUUID().toString();
    }
}