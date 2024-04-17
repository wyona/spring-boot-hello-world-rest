package org.wyona.webapp.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(description = "Send contact information (and optional attachment) to configured email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request, e.g. provided email parameter is not valid email address")
    })
    public ResponseEntity<Email> sendEmail(@Parameter(name = "emailAddress", description = "e-mail address of person sending contact information", required = true) @RequestPart String emailAddress,
                                           @Parameter(name = "name", description = "name of person sending contact information") @RequestPart(required = false) String name,
                                           @Parameter(name = "emailSubject", description = "e-mail subject") @RequestPart(required = false) String emailSubject,
                                           @Parameter(name = "emailText", description = "e-mail message") @RequestPart(required = false)  String emailText,
                                           @Parameter(name = "emailAttachment", description = "e-mail attachment") @RequestPart(name = "emailAttachment", required = false) MultipartFile emailAttachment) throws MessagingException {
        Email email = new Email(contactEmail, emailSubject, "Contact request from: " + emailAddress + ", " + name + "\n\nMessage: \n\n" + emailText, false).attachment(emailAttachment);

        email.setSubject(emailValidation.getDefaultSubjectIfSubjectEmpty(email.getSubject()));
        email.setText(emailValidation.getDefaultTextIfTextEmpty(email.getText()));

        mailerService.sendEmail(email);

        return new ResponseEntity<>(email, HttpStatus.OK);
    }
}
