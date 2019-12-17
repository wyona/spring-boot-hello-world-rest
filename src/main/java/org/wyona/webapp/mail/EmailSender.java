package org.wyona.webapp.mail;

import com.sun.mail.smtp.SMTPMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.wyona.webapp.interfaces.EmailValidation;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.util.Properties;

@Component
public class EmailSender {

    private final Session session;
    private final String fromEmail;
    private final EmailValidation emailValidation;

    @Autowired
    public EmailSender(EmailSenderCofig config, EmailValidation emailValidation){
        this.emailValidation = emailValidation;

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", true);

        // TODO: Seems to cause trouble, when the mail server uses a self-signed certificate
        //props.put("mail.smtp.starttls.enable", true);

        props.put("mail.smtp.host", config.getHost());
        props.put("mail.smtp.port", config.getPort());

        // TODO: Make from address configurable
        fromEmail = "greetings@" + config.getHost();

        session = Session.getInstance(
                props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(config.getUsername(), config.getPassword());
                    }
                }
        );
    }

    /**
     * Send greetings by email
     */
    public void sendEmailGreeting(String email, String subject, String text) throws MessagingException {
        // I like to validate input parameters of service public methods, to ensure that each client sends all the needed parameters.
        // I send runtime exceptions in this case, not forcing the clients to handle specific checked exceptions.
        Assert.isTrue(!StringUtils.isEmpty(email), "Email recipient must be specified");
        Assert.isTrue(emailValidation.isEmailValid(email), "Email recipient not in valid format");
        Assert.isTrue(!StringUtils.isEmpty(subject), "Email subject must be specified");
        Assert.isTrue(!StringUtils.isEmpty(text), "Email content must be specified");

        Message message = new SMTPMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject(subject);
        message.setText(text);
        Transport.send(message);
    }
}
