package org.wyona.webapp.controllers;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

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

    @Autowired
    public HelloWorldController(MailerService mailerService){
        this.mailerService = mailerService;
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
        logger.info(new Greeting().getGreeting());

        if(email != null && !email.isEmpty()) {
            mailerService.sendEmailGreeting(email, "Hello World!", "Hello World!");
        }

        return new ResponseEntity<>(new Greeting(), HttpStatus.OK);
    }
}
