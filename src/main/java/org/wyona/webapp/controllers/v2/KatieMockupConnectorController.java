package org.wyona.webapp.controllers.v2;

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
 * 'Katie Mockup Connector' Controller
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v2")
@AllArgsConstructor
public class KatieMockupConnectorController {
    private MailerService mailerService;
    private EmailValidation emailValidation;
    private GreetingService greetingService;

    /**
     * Create tenant
     */
    @PostMapping("/tenant")
    @ApiOperation(value = "Create tenant")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 200, response = String.class, message = "Tenant Id")
    })
    ResponseEntity<String> createTenant(@Valid @RequestBody LanguageEmail request,
            @RequestHeader(value = "Accept-Language", defaultValue = "de") String lang,
            @ApiParam(name = "name", value = "Name of person to be greeted, e.g. 'Michael'") @RequestParam(name = "name", required = true) String name
            ){
        return ResponseEntity.ok("TODO");
    }

    /**
     * Delete tenant
     */
    @DeleteMapping("/tenant/{domain-id}")
    @ApiOperation(value = "Delete tenant")
    ResponseEntity<?> deleteTenant(
            @ApiParam(name = "domain-id", value = "Katie domain ID", required = true)
            @PathVariable(name = "domain-id", required = true) String domainId
    ){
        log.info("TODO: Delete tenant associated with Katie domain ID '" + domainId + "' ...");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
