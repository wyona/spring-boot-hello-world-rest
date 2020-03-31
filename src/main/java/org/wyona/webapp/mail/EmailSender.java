package org.wyona.webapp.mail;

import com.sun.mail.smtp.SMTPMessage;
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

@Component
public class EmailSender {

    private final Session session;
    private final EmailValidation emailValidation;

    @Value("${from.email.address}")
    private String fromEmail;

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
    public void sendEmailGreeting(String email, String subject, String text, MultipartFile attachment) throws MessagingException {
        validateParameters(email, subject, text);

        Message message = composeMessage(email, subject, text, attachment);

        Transport.send(message);
    }

    private void validateParameters(String email, String subject, String text) {
        // I like to validate input parameters of service public methods, to ensure that each client sends all the needed parameters.
        // I send runtime exceptions in this case, not forcing the clients to handle specific checked exceptions.
        Assert.isTrue(!StringUtils.isEmpty(email), "Email recipient must be specified");
        Assert.isTrue(emailValidation.isEmailValid(email), "Email recipient not in valid format");
        Assert.isTrue(!StringUtils.isEmpty(subject), "Email subject must be specified");
        Assert.isTrue(!StringUtils.isEmpty(text), "Email content must be specified");
    }

    private Message composeMessage(String email, String subject, String text, MultipartFile attachment) throws MessagingException {
        Message message = new SMTPMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject(subject);

        Multipart multipart = new MimeMultipart();

        BodyPart messageText = new MimeBodyPart();
        messageText.setText(text);
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
