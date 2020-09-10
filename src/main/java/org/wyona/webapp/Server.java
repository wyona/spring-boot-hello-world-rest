package org.wyona.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import org.wyona.webapp.controllers.HelloWorldController;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
@ComponentScan
/**
 *
 */
public class Server extends SpringBootServletInitializer {

    /**
     *
     */
    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

    /**
     *
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Server.class);
    }
}
