package org.wyona.webapp.services;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class GreetingService {
    Configuration configuration;

    /**
     * @param lang Greeting in specific language
     * @param name Name of person which will be greeted, e.g. "Michael"
     */
    public String getGreetingText(String lang, String name) {
        try {
            // TODO: Check whether template for provided language exists
            Template template = configuration.getTemplate("greeting_" + lang + ".ftl");
            Map<String, Object> input = new HashMap<>();
            input.put("language", lang);
            input.put("name", name);
            StringWriter writer = new StringWriter();
            template.process(input, writer);
            return writer.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return e.getMessage();
        }
    }
}
