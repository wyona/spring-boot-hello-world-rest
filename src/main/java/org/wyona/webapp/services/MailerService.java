package org.wyona.webapp.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.wyona.webapp.interfaces.EmailValidation;
import org.wyona.webapp.mail.EmailSender;
import org.wyona.webapp.models.Email;
import org.wyona.webapp.models.Greeting;

import javax.mail.MessagingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MailerService {
    private final EmailSender emailSender;
    private final EmailValidation emailValidation;

    @Autowired
    public MailerService(EmailSender emailSender, EmailValidation emailValidation) {
        this.emailSender = emailSender;
        this.emailValidation = emailValidation;
    }

    /**
     * @param recipient E-Mail address of recipient
     * @param greeting Greeting which will be sent to recipient
     */
    public Greeting sendEmail(String recipient, Greeting greeting) throws MessagingException {
        // TODO: Check whether format of greeting body is HTML
        sendEmail(new Email(recipient, greeting.getGreeting(), greeting.getGreeting(), false));

        return greeting;
    }

    /**
     * Send email greeting
     * @param email Email address
     * @param subject Email subject
     * @param text Email body text
     * @param isHTMLMessage True when text is HTML and false when text is plain
     */
    public void sendEmail(String email, String subject, String text, boolean isHTMLMessage) throws MessagingException {
        sendEmail(new Email(email, subject, text, isHTMLMessage));
    }

    /**
     * @param email Email object, containing email address, subject and body text
     */
    public void sendEmail(Email email) throws MessagingException {
    	validateParameters(email.getEmail(), email.getSubject(), email.getText());
    	log.info("Sending email to {}", email.getEmail());
        emailSender.sendEmailGreeting(email.getEmail(), email.getSubject(), email.getText(), email.isHTMLMessage(), email.getAttachment());
    }

    /**
     * Validate email parameters
     */
    private void validateParameters(String email, String subject, String text) {
        // I like to validate input parameters of service public methods, to ensure that each client sends all the needed parameters.
        // I send runtime exceptions in this case, not forcing the clients to handle specific checked exceptions.
        Assert.isTrue(!StringUtils.isEmpty(email), "Email recipient must be specified");
        Assert.isTrue(emailValidation.isEmailValid(email), "Email recipient not in valid format");
        Assert.isTrue(!StringUtils.isEmpty(subject), "Email subject must be specified");
        Assert.isTrue(!StringUtils.isEmpty(text), "Email content must be specified");
    }
}
