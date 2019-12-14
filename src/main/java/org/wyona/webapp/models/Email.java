package org.wyona.webapp.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Email implements Serializable {

    private String email;
    private String subject;
    private String text;
	@JsonIgnore
	private Attachment attachment;

    // INFO: Default constructor is necessary, because otherwise a 400 is generated when using @RequestBody (see https://stackoverflow.com/questions/27006158/error-400-spring-json-requestbody-when-doing-post)
    public Email() {
    }

    /**
     *
     */
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
    public Attachment getAttachment() {
		return attachment;
	}

	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}
	
}
