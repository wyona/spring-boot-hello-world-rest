package org.wyona.webapp.controllers;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.wyona.webapp.models.Greeting;

/**
 *
 */
@RestController
@RequestMapping(value = "/api/greeting") 
public class HelloWorldController {

    private static final Logger logger = LogManager.getLogger("HelloWorldController");

    /**
     *
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Greeting> getGreeting() {
        logger.info(new Greeting().getGreeting());
        return new ResponseEntity<>(new Greeting(), HttpStatus.OK);
    }
}
