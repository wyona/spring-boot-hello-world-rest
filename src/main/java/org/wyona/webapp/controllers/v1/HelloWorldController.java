package org.wyona.webapp.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
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
@RequestMapping(value = "/api/v1/greeting")
@AllArgsConstructor
public class HelloWorldController {
    private MailerService mailerService;
    private EmailValidation emailValidation;
    private GreetingService greetingService;

    /**
     * Send greetings by email
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @Operation(description="Generate greeting and send greeting as email when address specified")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request, e.g. provided email parameter is not valid email address")
    })
    public ResponseEntity<Greeting> getGreeting(
        @Parameter(name = "email", description = "email address greeting will be sent to, e.g. 'michael.wechner@wyona.com'") @RequestParam(name = "email", required = false) String email
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
    @Operation(description = "Send an email with provided text and subject (and optional attachment) to email address which is specified")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request, e.g. provided email parameter is not valid email address")
    })
    // Decomposing Email object into separate parts, as Swagger has an issue with multipart requests.
    // If @RequestBody was used, Swagger would handle the conversion of JSON into Email object correctly and provide the mime type.
    // Swagger only provides a mime type for the entire message, but not for each part. For parts, the data is not given a type and
    // Spring defaults to application/octet-stream and does know how to convert it.
    public ResponseEntity<Email> sendEmail(@Parameter(name = "emailAddress", description = "e-mail to be sent to", required = true) @RequestPart String emailAddress,
                                           @Parameter(name = "emailSubject", description = "e-mail subject") @RequestPart(required = false) String emailSubject,
                                           @Parameter(name = "emailText", description = "e-mail text") @RequestPart(required = false)  String emailText,
                                           @Parameter(name = "emailAttachment", description = "e-mail attachment") @RequestPart(name = "emailAttachment", required = false) MultipartFile emailAttachment) throws MessagingException, InterruptedException {

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
    @Operation(description = "Send a language specific greeting to a provided email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = String.class)), description = "Email sent")
    })
    ResponseEntity<String> sendEmailWithSpecificLanguage(@Valid @RequestBody LanguageEmail request,
            @RequestHeader(value = "Accept-Language", defaultValue = "de") String lang,
            @Parameter(name = "name", description = "Name of person to be greeted, e.g. 'Michael'") @RequestParam(name = "name", required = true) String name
            ) throws MessagingException, InterruptedException{
        String greetingText = greetingService.getGreetingText(lang, name);
        mailerService.sendEmail(request.getEmail(), "Greeting in " + lang, greetingText, true);
        return ResponseEntity.ok(greetingText);
    }
}
