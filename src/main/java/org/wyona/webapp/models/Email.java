package org.wyona.webapp.models;

import java.io.Serializable;

public class Email implements Serializable {

    private String email;
    private String subject;
    private String text;

    public Email(String email, String subject, String text) {
        this.email = email;
        this.subject = subject;
        this.text = text;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
