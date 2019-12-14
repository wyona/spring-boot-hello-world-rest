package org.wyona.webapp.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface EmailValidation {

    Boolean isEmailValid(String email);

    String isSubjectEmpty(String subject);

    String isTextEmpty(String text);
    
	Boolean ifAttachmentExists(MultipartFile file);
}
