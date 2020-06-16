package org.wyona.webapp.services;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class GreetingService {
    Environment environment;
    Configuration configuration;

    /**
     * @param lang Greeting in specific language
     */
    public String getGreetingText(String lang) {
        try {
            String greeting = environment.getProperty(lang, "Hello World");
            Template template = configuration.getTemplate("greeting.ftl");
            Map<String, Object> input = new HashMap<>();
            input.put("greeting", greeting);
            StringWriter writer = new StringWriter();
            template.process(input, writer);
            return writer.toString();
        } catch (Exception e) {
            return "Hello World";
        }
    }
}
