package org.wyona.webapp.api;

import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * LanguageEmail
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen",
    date = "2019-12-20T15:03:27.963Z[GMT]")
public class LanguageEmail {
  @JsonProperty("email")
  private String email = null;

  @JsonProperty("languageCode")
  private String languageCode = null;

  public LanguageEmail email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * 
   * @return email
   **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LanguageEmail languageCode(String languageCode) {
    this.languageCode = languageCode;
    return this;
  }

  /**
   * Get languageCode
   * 
   * @return languageCode
   **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  public String getLanguageCode() {
    return languageCode;
  }

  public void setLanguageCode(String languageCode) {
    this.languageCode = languageCode;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LanguageEmail languageEmail = (LanguageEmail) o;
    return Objects.equals(this.email, languageEmail.email)
        && Objects.equals(this.languageCode, languageEmail.languageCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, languageCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LanguageEmail {\n");

    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    languageCode: ").append(toIndentedString(languageCode)).append("\n");
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
