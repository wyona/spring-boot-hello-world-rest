package org.wyona.webapp.mail;

import com.sun.mail.smtp.SMTPMessage;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.wyona.webapp.interfaces.EmailValidation;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;

@Slf4j
@Component
public class EmailSender {

    private final Session session;

    @Value("${from.email.address}")
    private String fromEmail;

    @Autowired
    public EmailSender(EmailSenderCofig config) {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", true);

        // TODO: Seems to cause trouble, when the mail server uses a self-signed certificate
        //props.put("mail.smtp.starttls.enable", true);

        props.put("mail.smtp.host", config.getHost());
        props.put("mail.smtp.port", config.getPort());

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
    @Async
    public void sendEmailGreeting(String email, String subject, String text, boolean isHTMLMessage, MultipartFile attachment) throws MessagingException {
        Message message = composeMessage(email, subject, text, isHTMLMessage, attachment);

        // TEST: Uncomment lines below to test thread
/*
        try {
            for (int i = 0; i < 5; i++) {
                log.info("Sleep for 2 seconds ...");
                Thread.sleep(2000);
            }
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
*/

        Transport.send(message);
        log.info("Email sent to {}", email);
    }

    /**
     *
     */
    private Message composeMessage(String email, String subject, String text, boolean isHTMLMessage, MultipartFile attachment) throws MessagingException {
        Message message = new SMTPMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject(subject);

        Multipart multipart = new MimeMultipart();

        String mimeType = "text/plain";
        if (isHTMLMessage) {
            mimeType = "text/html";
        }
        BodyPart messageText = new MimeBodyPart();
        messageText.setContent(text, mimeType);

        multipart.addBodyPart(messageText);

        if (attachment != null) {
            try {
                ByteArrayDataSource dataSource = new ByteArrayDataSource(attachment.getInputStream(), attachment.getContentType());

                MimeBodyPart messageAttachment = new MimeBodyPart();
                messageAttachment.setDataHandler(new DataHandler(dataSource));
                messageAttachment.setFileName(attachment.getOriginalFilename());

                multipart.addBodyPart(messageAttachment);
            } catch (IOException e) {
                throw new MessagingException(e.getMessage(), e);
            }
        }

        message.setContent(multipart);

        return message;
    }
}
