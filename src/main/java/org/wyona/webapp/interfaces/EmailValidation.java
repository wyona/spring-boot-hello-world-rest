package org.wyona.webapp.interfaces;

public interface EmailValidation {
    // Moved to interface so that the regex does not get repeated
    String EMAIL_VALIDATION_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

    Boolean isEmailValid(String email);

    // Renamed, as is... implies a boolean return value. I usually go with the approach of making the name of the method descriptive enough
    // , so that it tells the story and the user of the method does not need to look at the implementation.
    String getDefaultSubjectIfSubjectEmpty(String subject);

    String getDefaultTextIfTextEmpty(String text);
}
