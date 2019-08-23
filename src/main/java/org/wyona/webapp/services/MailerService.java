package org.wyona.webapp.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wyona.webapp.mail.EmailSender;

import javax.mail.MessagingException;

@Component
public class MailerService {
    private static final Logger logger = LogManager.getLogger("MailerService");

    private final EmailSender emailSender;

    @Autowired
    public MailerService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendEmailGreeting(String email, String subject, String text) throws MessagingException {
        emailSender.sendEmailGreeting(email, subject, text);
        logger.info("Email greeting sent to {}", email);
    }
}
