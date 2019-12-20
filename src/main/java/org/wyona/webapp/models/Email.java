package org.wyona.webapp.models;

import java.io.Serializable;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class Email implements Serializable {

  private String email;
  private String subject;
  private String text;
  private List<MultipartFile> files;

  // INFO: Default constructor is necessary, because otherwise a 400 is generated when using
  // @RequestBody (see
  // https://stackoverflow.com/questions/27006158/error-400-spring-json-requestbody-when-doing-post)
  public Email() {}

  /**
   *
   */
  public Email(String email, String subject, String text) {
    this.email = email;
    this.subject = subject;
    this.text = text;
  }

  public Email(String email) {
    this.email = email;

  }

  public Email(String email, String subject, String text, List<MultipartFile> files) {
    this.email = email;
    this.subject = subject;
    this.text = text;
    this.files = files;
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

  public List<MultipartFile> getFiles() {
    return files;
  }

  public void setFiles(List<MultipartFile> files) {
    this.files = files;
  }



}
