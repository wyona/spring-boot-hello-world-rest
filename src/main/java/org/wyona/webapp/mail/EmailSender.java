package org.wyona.webapp.mail;

import com.sun.mail.smtp.SMTPMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wyona.webapp.models.Attachment;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

@Component
public class EmailSender {
	
	private static final Logger logger = LogManager.getLogger("EmailSender");
	private static final String ENCODING = "UTF-8";
	private static final String CONTENT_TYPE = "Content-Type";

    private final Session session;
    private final String fromEmail;

    @Autowired
    public EmailSender(EmailSenderCofig config){

        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", true);

        // TODO: Seems to cause trouble, when the mail server uses a self-signed certificate
        props.put("mail.smtp.starttls.enable", true);

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
		sendEmailGreeting(email, subject, text, null);
	}

	public void sendEmailGreeting(String email, String subject, String text, Attachment attachment)
			throws MessagingException {
		Message message = new SMTPMessage(session);
		message.setFrom(new InternetAddress(fromEmail));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
		message.setSubject(subject);
		if (attachment != null) {
			message.setContent(prepareContent(text, attachment));
		} else {
			message.setText(text);
		}
		message.setSentDate(new Date());
		Transport.send(message);
	}

	private static Multipart prepareContent(String text, Attachment attachment) throws MessagingException {
		final MimeBodyPart bodyPart1 = new MimeBodyPart();
		bodyPart1.setText(text, ENCODING);
		bodyPart1.setHeader(CONTENT_TYPE, "text/html; charset=\"UTF-8\"");

		final Multipart multipart = new MimeMultipart("related");
		multipart.addBodyPart(bodyPart1);
		try {
			if (attachment.getBdata() != null) {
				final MimeBodyPart bodyPart2 = new MimeBodyPart();
				final ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(attachment.getBdata(),
						"application/octet-stream");
				bodyPart2.setDisposition(Part.ATTACHMENT);
				bodyPart2.setFileName(MimeUtility.encodeText(attachment.getFilename()));
				bodyPart2.setDataHandler(new DataHandler(byteArrayDataSource));
				multipart.addBodyPart(bodyPart2);
			}
		} catch (IOException e) {
			logger.error("Error occures while sending attachment {}", e.getMessage());
		}
		return multipart;
	}

}
