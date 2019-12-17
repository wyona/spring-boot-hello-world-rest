package org.wyona.webapp.controllers;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

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

        // Calling the overloaded method with default values encapsulated in the service class
        return new ResponseEntity<>(mailerService.sendEmail(email), HttpStatus.OK);
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

        //removed email recipient validation, since it is done in mailerService.sendEmail for all clients

        //automated subject and text values were not used.
        email.setSubject(emailValidation.getDefaultSubjectIfSubjectEmpty(email.getSubject()));
        email.setText(emailValidation.getDefaultTextIfTextEmpty(email.getText()));

        mailerService.sendEmail(email);

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

        mailerService.sendEmail(request.getEmail(), "Greeting in " + language.name(), language.getMessage());
        return new ResponseEntity<>(language, HttpStatus.OK);
    }
}
