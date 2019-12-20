package org.wyona.webapp.controllers;

import java.io.IOException;
import java.util.List;
import javax.mail.MessagingException;
// INFO: Validator implementation provided by 'hibernate-validator' (see pom file)
import javax.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.wyona.webapp.api.EmailResponse;
import org.wyona.webapp.api.SendEmailApi;
// INFO: See alternative E-Mail validation approach below using @Valid annotation
import org.wyona.webapp.interfaces.EmailValidation;
import org.wyona.webapp.models.Email;
import org.wyona.webapp.models.Greeting;
import org.wyona.webapp.models.LanguageEmail;
import org.wyona.webapp.services.MailerService;
import io.swagger.annotations.ApiParam;

/**
 * 'Hello World' Controller
 */
@RestController
@RequestMapping(value = "/api")
public class HelloWorldController implements SendEmailApi {

  private static final Logger logger = LogManager.getLogger("HelloWorldController");

  private MailerService mailerService;

  private EmailValidation emailValidation;

  @Autowired
  public HelloWorldController(MailerService mailerService, EmailValidation emailValidation) {
    this.mailerService = mailerService;
    this.emailValidation = emailValidation;
  }

  /**
   * Send email with attachments
   * 
   * @throws IOException
   */

  @Override
  @RequestMapping(value = "/send", method = RequestMethod.POST)
  public ResponseEntity<EmailResponse> sendEmail(@RequestParam("email") String email,
      @RequestParam("subject") String subject, @RequestParam("text") String text,
      @RequestParam("files") List<MultipartFile> files) throws IOException {

    Email emailAtt = new Email(email, subject, text, files);
    return new ResponseEntity<EmailResponse>(mailerService.sendEmail(emailAtt), HttpStatus.OK);
  }

  /**
   * Send greetings by email for a specific language
   * 
   * @throws IOException
   */
  @Override
  @RequestMapping(value = "/send/lang", method = RequestMethod.POST)
  public ResponseEntity<LanguageEmail.Language> sendEmailWithSpecificLanguage(
      @Valid @RequestBody LanguageEmail langEmail) throws MessagingException, IOException {
    return new ResponseEntity<LanguageEmail.Language>(mailerService.sendEmailGreeting(langEmail),
        HttpStatus.OK);
  }

  /**
   * Send greetings by email
   * 
   * @throws IOException
   */
  @Override
  @RequestMapping(value = "/send/greeting", method = RequestMethod.GET,
      produces = "application/json")
  public ResponseEntity<Greeting> getGreeting(
      @ApiParam(name = "email",
          value = "email address greeting will be sent to, e.g. 'michael.wechner@wyona.com'",
          required = false) @RequestParam(name = "email", required = false) String email)
      throws MessagingException, IOException {
    return new ResponseEntity<Greeting>(mailerService.sendEmailGreeting(email), HttpStatus.OK);
  }

}
