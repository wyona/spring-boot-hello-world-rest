package org.wyona.webapp.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileSender {
    private String fileName;
    private int numberOfFiles;

    @Autowired
    public FileSender(){
    }

    public String getFileName() {
        return fileName;
    }

    public int getNumberOfFiles() {
        return numberOfFiles;
    }

    // TODO: Make from all needed methods...
}
