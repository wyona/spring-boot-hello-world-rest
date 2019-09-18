package org.wyona.webapp.controllers;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.wyona.webapp.interfaces.EmailValidation;
import org.wyona.webapp.models.Email;
import org.wyona.webapp.models.Greeting;
import org.wyona.webapp.services.MailerService;

import javax.mail.MessagingException;

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
    @ApiOperation(value = "Send an e-mail with provided text, subject to email address which is specified")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, e.g. provided email parameter is not valid email address")
    })
    public ResponseEntity<Email> sendEmail(@ApiParam(name = "e-mail", value = "e-mail address to be sent to", required = true)
                                              @RequestParam(name = "e-mail") String email,
                                           @ApiParam(name = "subject", value = "subject to be sent to")
                                              @RequestParam(name = "subject") String subject,
                                           @ApiParam(name = "text", value = "text to be sent to")
                                              @RequestParam(name = "text") String text) throws MessagingException {

        Boolean emailValid = emailValidation.isEmailValid(email);
        if (!emailValid) {
            throw new IllegalArgumentException("Provided email is not valid");
        }
        String automatedSubject = emailValidation.isSubjectEmpty(subject);
        String automatedText = emailValidation.isTextEmpty(text);

        Email object = new Email(email, automatedSubject, automatedText);
        mailerService.sendEmailToUser(object);

        return new ResponseEntity<>(object, HttpStatus.OK);
    }
}
