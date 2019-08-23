package org.wyona.webapp.mail;

import com.sun.mail.smtp.SMTPMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.util.Properties;

@Component
public class EmailSender {

    private final Session session;
    private final String fromEmail;

    @Autowired
    public EmailSender(EmailSenderCofig config){

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
        Message message = new SMTPMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject(subject);
        message.setText(text);
        Transport.send(message);
    }
}
