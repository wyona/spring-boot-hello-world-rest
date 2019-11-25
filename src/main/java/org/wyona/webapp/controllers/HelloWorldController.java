package org.wyona.webapp.controllers;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// INFO: See alternative E-Mail validation approach below using @Valid annotation
import org.wyona.webapp.interfaces.EmailValidation;
import org.wyona.webapp.models.Email;
import org.wyona.webapp.models.Greeting;
import org.wyona.webapp.models.LanguageEmail;
import org.wyona.webapp.services.MailerService;

import javax.mail.MessagingException;

// INFO: Validator implementation provided by 'hibernate-validator' (see pom file)
import javax.validation.Valid;

/**
 * 'Hello World' Controller 
 */
@RestController
@RequestMapping(value = "/api/greeting")
public class HelloWorldController {

    private static final Logger logger = LogManager.getLogger("HelloWorldController");

    private MailerService mailerService;

    private EmailValidation emailValidation;

    @Autowired
    public HelloWorldController(MailerService mailerService, EmailValidation emailValidation){
        this.mailerService = mailerService;
        this.emailValidation = emailValidation;
    }

    /**
     * Send greetings by email
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value="Generate greeting and send greeting as email when address specified")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, e.g. provided email parameter is not valid email address")
    })
    public ResponseEntity<Greeting> getGreeting(
        @ApiParam(name = "email", value = "email address greeting will be sent to, e.g. 'michael.wechner@wyona.com'", required = false) @RequestParam(name = "email", required = false) String email
        // TODO: Use @javax.validation.constraints.Email to validate email address 
        //@ApiParam(name = "email", value = "email address greeting will be sent to, e.g. 'michael.wechner@wyona.com'", required = false) @javax.validation.constraints.Email(message = "Email should be valid") @RequestParam(name = "email", required = false) String email
        ) throws MessagingException {

        Greeting greeting = new Greeting("World");
        logger.info(greeting.getGreeting());

        if(email != null && !email.isEmpty()) {
            mailerService.sendEmailGreeting(email, greeting.getGreeting(), greeting.getGreeting());
        }

        return new ResponseEntity<>(greeting, HttpStatus.OK);
    }

    /**
     * Send greetings by email, whereas subject and body text can be set
     */
    @PostMapping("/send")
    @ApiOperation(value = "Send an email with provided text and subject to email address which is specified")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, e.g. provided email parameter is not valid email address")
    })
    public ResponseEntity<Email> sendEmail(@ApiParam(name = "email", value = "e-mail to be sent to", required = true) @RequestBody Email email) throws MessagingException {

        Boolean emailValid = emailValidation.isEmailValid(email.getEmail());
        if (!emailValid) {
            throw new IllegalArgumentException("Provided email is not valid");
        }
        String automatedSubject = emailValidation.isSubjectEmpty(email.getSubject());
        String automatedText = emailValidation.isTextEmpty(email.getText());

        mailerService.sendEmailToUser(email);

        return new ResponseEntity<>(email, HttpStatus.OK);
    }

    /**
     * Send greetings by email for a specific language
     */
    @PostMapping("/send/lang")
    @ApiOperation(value = "Send a language specific greeting to a provided email address")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 200, response = String.class, message = "Email sent")
    })
    public ResponseEntity<LanguageEmail.Language> sendEmailWithSpecificLanguage(@Valid @RequestBody LanguageEmail request) throws MessagingException {

        LanguageEmail.Language language = LanguageEmail.Language.getLanguageByCode(request.getLanguageCode());
        if (language == null) {
            throw new IllegalArgumentException("Language '" + request.getLanguageCode() + "' not supported yet!");
        }

        mailerService.sendEmailGreeting(request.getEmail(), "Greeting in " + language.name(), language.getMessage());
        return new ResponseEntity<>(language, HttpStatus.OK);
    }
}
