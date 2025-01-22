package com.example.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class EmailService {

    private JavaMailSender emailSender;

    public void sendEmail(String to, String text) {
        var message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Library notification");
        message.setText(text);

        log.info("Sending email to:{} message:{}", message.getTo(), message.getText());
        emailSender.send(message);
    }

}
