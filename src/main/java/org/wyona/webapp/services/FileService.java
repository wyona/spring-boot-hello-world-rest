package org.wyona.webapp.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.wyona.webapp.mail.FileSender;

@Component
public class FileService {
    private static final Logger logger = LogManager.getLogger("MailerService");

    private FileSender fileSender;

    @Autowired
    public FileService(FileSender fileSender) {
        this.fileSender = fileSender;
    }

    public static Logger getLogger() {
        return logger;
    }

    public FileSender getFileSender() {
        return fileSender;
    }

    public void setFileSender(FileSender fileSender) {
        this.fileSender = fileSender;
    }

    public void uploadFile (MultipartFile file)
    {
        if(file != null)
        {
            //TODO: Make implementation for uploadFile
        }

    }

    // TODO: Make from all needed methods...
}
