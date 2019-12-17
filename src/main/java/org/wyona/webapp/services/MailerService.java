package org.wyona.webapp.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.wyona.webapp.mail.EmailSender;
import org.wyona.webapp.models.Email;
import org.wyona.webapp.models.Greeting;

import javax.mail.MessagingException;

@Component
public class MailerService {
    private static final Logger logger = LogManager.getLogger("MailerService");
    private static final String DEFAULT_GREETING = "World";

    private final EmailSender emailSender;

    @Autowired
    public MailerService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    // Overloading the method with instantiating the default Greeting in the service class instead of the controller.
    // Service classes are easier to unit test, and overloading the method makes the code cleaner and easier to follow and test,
    // removing the code duplication in the process.
    public Greeting sendEmail(String recipient) throws MessagingException {
        Greeting defaultGreeting = new Greeting(DEFAULT_GREETING);

        sendEmail(new Email(recipient, defaultGreeting.getGreeting(), defaultGreeting.getGreeting()));

        return defaultGreeting;
    }

    /**
     * Send email greeting
     * @param email Email address
     * @param subject Email subject
     * @param text Email body text
     */
    public void sendEmail(String email, String subject, String text) throws MessagingException {
        sendEmail(new Email(email, subject, text));
    }

    /**
     * @param email Email object, containing email address, subject and body text
     */
    public void sendEmail(Email email) throws MessagingException {
        emailSender.sendEmailGreeting(email.getEmail(), email.getSubject(), email.getText(), email.getAttachment());

        // Consider using AOP to log around methods (log method name, input parameters, return values, exceptions, etc.). This would move the logging logic to one place, and configuring pointcuts would allow the logger to be configurable.
        logger.info("Email sent to {}", email.getEmail());
    }
}
