package org.wyona.webapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wyona.webapp.interfaces.EmailValidation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LanguageEmail implements Serializable {
    @NotBlank(message = "Email cannot be empty or null")
    @Email(regexp = EmailValidation.EMAIL_VALIDATION_REGEX, message = "Submitted email is not valid")
    private String email;
}
