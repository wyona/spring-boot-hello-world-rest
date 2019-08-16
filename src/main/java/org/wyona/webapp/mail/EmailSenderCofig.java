package org.wyona.webapp.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class EmailSenderCofig {

    @Value("${app.mail.host}")
    private String host;

    @Value("${app.mail.port}")
    private int port;

    @Value("${app.mail.username}")
    private String username;

    @Value("${app.mail.password}")
    private String password;

    String getHost() {
        return host;
    }

    int getPort() {
        return port;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }
}
