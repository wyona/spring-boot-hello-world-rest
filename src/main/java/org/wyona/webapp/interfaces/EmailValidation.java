package org.wyona.webapp.interfaces;

public interface EmailValidation {

    Boolean isEmailValid(String email);

    String isSubjectEmpty(String subject);

    String isTextEmpty(String text);
}
