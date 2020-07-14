package org.wyona.webapp.controllers;

import io.swagger.annotations.*;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// INFO: See alternative E-Mail validation approach below using @Valid annotation
import org.springframework.web.multipart.MultipartFile;
import org.wyona.webapp.interfaces.EmailValidation;
import org.wyona.webapp.models.Email;
import org.wyona.webapp.models.Greeting;
import org.wyona.webapp.models.LanguageEmail;
import org.wyona.webapp.services.GreetingService;
import org.wyona.webapp.services.MailerService;

import javax.mail.MessagingException;

import lombok.extern.slf4j.Slf4j;

// INFO: Validator implementation provided by 'hibernate-validator' (see pom file)
import javax.validation.Valid;

/**
 * 'Hello World' Controller 
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/greeting")
@AllArgsConstructor
public class HelloWorldController {
    private MailerService mailerService;
    private EmailValidation emailValidation;
    private GreetingService greetingService;

    /**
     * Send greetings by email
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value="Generate greeting and send greeting as email when address specified")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, e.g. provided email parameter is not valid email address")
    })
    public ResponseEntity<Greeting> getGreeting(
        @ApiParam(name = "email", value = "email address greeting will be sent to, e.g. 'michael.wechner@wyona.com'") @RequestParam(name = "email", required = false) String email
        // TODO: Use @javax.validation.constraints.Email to validate email address 
        //@ApiParam(name = "email", value = "email address greeting will be sent to, e.g. 'michael.wechner@wyona.com'", required = false) @javax.validation.constraints.Email(message = "Email should be valid") @RequestParam(name = "email", required = false) String email
        ) throws MessagingException {

        log.info("Send greeting to '" + email + "'...");
        return new ResponseEntity<>(mailerService.sendEmail(email, new Greeting("World")), HttpStatus.OK);
    }

    /**
     * Send greetings by email, whereas subject and body text can be set
     * @throws InterruptedException 
     */
    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Send an email with provided text and subject (and optional attachment) to email address which is specified")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, e.g. provided email parameter is not valid email address")
    })
    // Decomposing Email object into separate parts, as Swagger has an issue with multipart requests.
    // If @RequestBody was used, Swagger would handle the conversion of JSON into Email object correctly and provide the mime type.
    // Swagger only provides a mime type for the entire message, but not for each part. For parts, the data is not given a type and
    // Spring defaults to application/octet-stream and does know how to convert it.
    public ResponseEntity<Email> sendEmail(@ApiParam(name = "emailAddress", value = "e-mail to be sent to", required = true) @RequestPart String emailAddress,
                                           @ApiParam(name = "emailSubject", value = "e-mail subject") @RequestPart(required = false) String emailSubject,
                                           @ApiParam(name = "emailText", value = "e-mail text") @RequestPart(required = false)  String emailText,
                                           @ApiParam(name = "emailAttachment", value = "e-mail attachment") @RequestPart(name = "emailAttachment", required = false) MultipartFile emailAttachment) throws MessagingException, InterruptedException {

        // TODO: Check whether format of emailText is HTML
        Email email = new Email(emailAddress, emailSubject, emailText, false).attachment(emailAttachment);
        //removed email recipient validation, since it is done in mailerService.sendEmail for all clients

        //automated subject and text values were not used.
        email.setSubject(emailValidation.getDefaultSubjectIfSubjectEmpty(email.getSubject()));
        email.setText(emailValidation.getDefaultTextIfTextEmpty(email.getText()));

        mailerService.sendEmail(email);

        return new ResponseEntity<>(email, HttpStatus.OK);
    }

    /**
     * Send greetings by email for a specific language
     * @throws InterruptedException 
     */
    @PostMapping("/send/lang")
    @ApiOperation(value = "Send a language specific greeting to a provided email address")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 200, response = String.class, message = "Email sent")
    })
    ResponseEntity<String> sendEmailWithSpecificLanguage(@Valid @RequestBody LanguageEmail request,
            @RequestHeader(value = "Accept-Language", defaultValue = "de") String lang,
            @ApiParam(name = "name", value = "Name of person to be greeted, e.g. 'Michael'") @RequestParam(name = "name", required = true) String name
            ) throws MessagingException, InterruptedException{
        String greetingText = greetingService.getGreetingText(lang, name);
        mailerService.sendEmail(request.getEmail(), "Greeting in " + lang, greetingText, true);
        return ResponseEntity.ok(greetingText);
    }
}
