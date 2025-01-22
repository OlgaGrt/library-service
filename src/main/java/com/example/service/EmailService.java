package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private JavaMailSender emailSender;

    public void sendEmail(String to, String text) {
        var message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Library notification");
        message.setText(text);

        logger.info("Sending email to:{} message:{}", message.getTo(), message.getText());
        emailSender.send(message);
    }

}
