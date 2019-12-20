package org.wyona.webapp.services;

import java.io.IOException;
import javax.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wyona.webapp.api.EmailResponse;
import org.wyona.webapp.mail.EmailSender;
import org.wyona.webapp.models.Email;
import org.wyona.webapp.models.Greeting;
import org.wyona.webapp.models.LanguageEmail;
import org.wyona.webapp.models.LanguageEmail.Language;
import org.wyona.webapp.validations.EmailValidationImpl;

@Component
public class MailerService {
  private static final Logger logger = LogManager.getLogger("MailerService");

  private final EmailSender emailSender;

  @Autowired
  EmailValidationImpl emailValidation;


  @Autowired
  public MailerService(EmailSender emailSender) {
    this.emailSender = emailSender;
  }

  /**
   * Send email greeting
   * 
   * @param email Email address
   * @param subject Email subject
   * @param text Email body text
   * @throws IOException
   */
  public Language sendEmailGreeting(LanguageEmail langEmail)
      throws MessagingException, IOException {

    LanguageEmail.Language language =
        LanguageEmail.Language.getLanguageByCode(langEmail.getLanguageCode());
    if (language == null) {
      throw new IllegalArgumentException(
          "Language '" + langEmail.getLanguageCode() + "' not supported yet!");
    }

    Email email =
        new Email(langEmail.getEmail(), "Greeting in " + language.name(), language.getMessage());

    emailSender.sendEmail(email);

    logger.info("Email greeting sent to {}", langEmail);

    return language;
  }


  public EmailResponse sendEmail(Email email) throws IOException {
    Boolean emailValid = emailValidation.isEmailValid(email.getEmail());
    if (!emailValid) {
      throw new IllegalArgumentException("Provided email is not valid");
    }
    emailSender.sendEmail(email);
    return EmailMapper.mapToApi(email);
  }

  public Greeting sendEmailGreeting(String email) throws IOException {
    Greeting greeting = new Greeting("World");
    logger.info(greeting.getGreeting());
    Boolean emailValid = emailValidation.isEmailValid(email);
    if (email != null && !email.isEmpty() || emailValid) {
      Email emailObj = new Email(email, greeting.getGreeting(), greeting.getGreeting());
      emailSender.sendEmail(emailObj);
    }
    return greeting;


  }
}
