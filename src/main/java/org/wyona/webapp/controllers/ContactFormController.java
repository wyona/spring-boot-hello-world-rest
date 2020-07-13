package org.wyona.webapp.controllers;

import io.swagger.annotations.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

// INFO: See alternative E-Mail validation approach below using @Valid annotation
import org.springframework.web.multipart.MultipartFile;
import org.wyona.webapp.interfaces.EmailValidation;
import org.wyona.webapp.models.Email;
import org.wyona.webapp.models.Greeting;
import org.wyona.webapp.models.LanguageEmail;
import org.wyona.webapp.services.MailerService;

import javax.mail.MessagingException;

import lombok.extern.slf4j.Slf4j;

// INFO: Validator implementation provided by 'hibernate-validator' (see pom file)
import javax.validation.Valid;

/**
 * 'Contact Form' Controller 
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1")
public class ContactFormController {

    private MailerService mailerService;

    private EmailValidation emailValidation;

    @Value("${contact.email}")
    private String contactEmail;

    @Autowired
    public ContactFormController(MailerService mailerService, EmailValidation emailValidation){
        this.mailerService = mailerService;
        this.emailValidation = emailValidation;
    }

    /**
     * Send contact information by email
     */
    @PostMapping(value = "/contact", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Send contact information (and optional attachment) to configured email address")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, e.g. provided email parameter is not valid email address")
    })
    public ResponseEntity<Email> sendEmail(@ApiParam(name = "emailAddress", value = "e-mail address of person sending contact information", required = true) @RequestPart String emailAddress,
                                           @ApiParam(name = "name", value = "name of person sending contact information") @RequestPart(required = false) String name,
                                           @ApiParam(name = "emailSubject", value = "e-mail subject") @RequestPart(required = false) String emailSubject,
                                           @ApiParam(name = "emailText", value = "e-mail message") @RequestPart(required = false)  String emailText,
                                           @ApiParam(name = "emailAttachment", value = "e-mail attachment") @RequestPart(name = "emailAttachment", required = false) MultipartFile emailAttachment) throws MessagingException {
        Email email = new Email(contactEmail, emailSubject, "Contact request from: " + emailAddress + ", " + name + "\n\nMessage: \n\n" + emailText, false).attachment(emailAttachment);

        email.setSubject(emailValidation.getDefaultSubjectIfSubjectEmpty(email.getSubject()));
        email.setText(emailValidation.getDefaultTextIfTextEmpty(email.getText()));

        mailerService.sendEmail(email);

        return new ResponseEntity<>(email, HttpStatus.OK);
    }
}
