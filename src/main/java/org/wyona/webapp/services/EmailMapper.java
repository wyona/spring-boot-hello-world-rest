package org.wyona.webapp.services;

import org.springframework.stereotype.Component;
import org.wyona.webapp.api.EmailResponse;
import org.wyona.webapp.models.Email;

@Component
public class EmailMapper {

  public static EmailResponse mapToApi(Email email) {
    return new EmailResponse().email(email.getEmail()).subject(email.getSubject())
        .text(email.getText());
  }

}
