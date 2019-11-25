package org.wyona.webapp.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wyona.webapp.mail.EmailSender;
import org.wyona.webapp.models.Email;

import javax.mail.MessagingException;

@Component
public class MailerService {
    private static final Logger logger = LogManager.getLogger("MailerService");

    private final EmailSender emailSender;

    @Autowired
    public MailerService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    /**
     * Send email greeting
     * @param email Email address
     * @param subject Email subject
     * @param text Email body text
     */
    public void sendEmailGreeting(String email, String subject, String text) throws MessagingException {
        emailSender.sendEmailGreeting(email, subject, text);
        logger.info("Email greeting sent to {}", email);
    }

    /**
     * @param object Email object, containing email address, subject and body text
     */
    public void sendEmailToUser(Email object) throws MessagingException {
        emailSender.sendEmailGreeting(object.getEmail(), object.getSubject(), object.getText());
        logger.info("Email sent to {}", object.getEmail());
    }
}
