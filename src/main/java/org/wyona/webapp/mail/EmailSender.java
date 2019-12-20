package org.wyona.webapp.mail;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.wyona.webapp.models.Email;

@Component
public class EmailSender {

  private final Session session;
  private final String fromEmail;



  @Autowired
  public EmailSender(EmailSenderCofig config) {

    Properties props = new Properties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", true);
    props.put("mail.smtp.starttls.enable", "true");

    // TODO: Seems to cause trouble, when the mail server uses a self-signed certificate
    // props.put("mail.smtp.starttls.enable", true);

    props.put("mail.smtp.host", config.getHost());
    props.put("mail.smtp.port", config.getPort());

    // TODO: Make from address configurable
    fromEmail = "greetings@" + config.getHost();

    session = Session.getInstance(props, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(config.getUsername(), config.getPassword());
      }
    });
  }

  /**
   * Send email with attachment
   * 
   * @throws IOException
   */
  public void sendEmail(Email email) throws IOException {
    try {

      MimeMessage msg = new MimeMessage(session);
      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getEmail()));
      msg.setSubject(email.getSubject());
      msg.setText(email.getText());

      // Create the message body part
      BodyPart messageBodyPart = new MimeBodyPart();
      messageBodyPart.setText(email.getText());

      Multipart multipart = new MimeMultipart();

      multipart.addBodyPart(messageBodyPart);

      messageBodyPart = new MimeBodyPart();

      // if there are attachments set attachments in email
      if (email.getFiles() != null) {
        addAttachments(email.getFiles(), messageBodyPart, multipart, msg);
        msg.setContent(multipart);
      }

      // Send message
      Transport.send(msg);

    } catch (MessagingException e) {
      throw new RuntimeException(e.getMessage());
    }
  }



  // Loop a list of attachments in email
  private void addAttachments(List<MultipartFile> files, BodyPart messageBodyPart,
      Multipart multipart, MimeMessage msg) throws MessagingException, IOException {
    files.forEach(file -> {
      try {
        addAttachment(file, messageBodyPart, multipart, msg);
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage());
      }
    });

  }



  // For each attachment in an email set its fileName and attachment in multipart
  private void addAttachment(MultipartFile file, BodyPart messageBodyPart, Multipart multipart,
      MimeMessage msg) throws IOException {

    try {
      messageBodyPart = new MimeBodyPart();
      DataSource source = new ByteArrayDataSource(file.getBytes(), file.getContentType());
      messageBodyPart.setDataHandler(new DataHandler(source));
      messageBodyPart.setFileName(file.getOriginalFilename());
      messageBodyPart.setDisposition(Part.ATTACHMENT);
      multipart.addBodyPart(messageBodyPart);

    } catch (MessagingException ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }
}
