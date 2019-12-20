package org.wyona.webapp.api;

import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Email with Attachment
 */
@ApiModel(description = "Email with Attachment")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen",
    date = "2019-12-14T12:15:06.169Z[GMT]")
public class EmailResponse {
  @JsonProperty("email")
  private String email = null;

  @JsonProperty("subject")
  private String subject = null;

  @JsonProperty("text")
  private String text = null;

  @JsonIgnore
  @JsonProperty("files")
  private List<MultipartFile> files = null;

  public EmailResponse() {
    // TODO Auto-generated constructor stub
  }

  public EmailResponse(String email, String subject, String text, List<MultipartFile> files) {

    this.email = email;
    this.subject = subject;
    this.text = text;
    this.files = files;
  }

  public EmailResponse(String email, String subject, String text) {

    this.email = email;
    this.subject = subject;
    this.text = text;
  }

  public EmailResponse email(String email) {
    this.email = email;
    return this;
  }

  /**
   * example@gmail.com
   * 
   * @return email
   **/
  @ApiModelProperty(example = "example@gmail.com", value = "example@gmail.com")

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public EmailResponse subject(String subject) {
    this.subject = subject;
    return this;
  }

  /**
   * Subject of an email
   * 
   * @return subject
   **/
  @ApiModelProperty(example = "This is subject", value = "Subject of an email")

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public EmailResponse text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Body of an email
   * 
   * @return text
   **/
  @ApiModelProperty(example = "Hi there! How are you?", value = "Body of an email")

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public EmailResponse files(List<MultipartFile> files) {
    this.files = files;
    return this;
  }

  /**
   * Files attached in email
   * 
   * @return files
   **/
  @ApiModelProperty(example = "[\"text.png\",\"text2.txt\"]", value = "Files attached in email")

  @Valid
  public List<MultipartFile> getFiles() {
    return files;
  }

  public void setFiles(List<MultipartFile> files) {
    this.files = files;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmailResponse emailWithAttachment = (EmailResponse) o;
    return Objects.equals(this.email, emailWithAttachment.email)
        && Objects.equals(this.subject, emailWithAttachment.subject)
        && Objects.equals(this.text, emailWithAttachment.text)
        && Objects.equals(this.files, emailWithAttachment.files);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, subject, text, files);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EmailWithAttachment {\n");

    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    subject: ").append(toIndentedString(subject)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    files: ").append(toIndentedString(files)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
