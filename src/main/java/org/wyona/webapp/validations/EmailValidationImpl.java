package org.wyona.webapp.validations;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wyona.webapp.interfaces.EmailValidation;

@Service
public class EmailValidationImpl implements EmailValidation {

    @Override
    public Boolean isEmailValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    @Override
    public String isSubjectEmpty(String subject) {
        if (subject == null || subject.isEmpty()) {
            return "Greetings";
        }
        return subject;
    }

    @Override
    public String isTextEmpty(String text) {
        if (text == null || text.isEmpty()) {
            return "Machine says hello.";
        }
        return text;
    }
    
    @Override
	public Boolean ifAttachmentExists(MultipartFile file) {
		if (file != null && !file.isEmpty()) {
			return true;
		}
		return false;
	}
}
