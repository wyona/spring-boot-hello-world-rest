package org.wyona.webapp.services;

import org.junit.Test;
import org.wyona.webapp.mail.FileSender;

import static org.junit.Assert.*;

public class FileServiceJUnitTest {

    private FileService fileService;
    private FileSender fileSender;

    @org.junit.Before
    public void setup (){
        this.fileSender = new FileSender();
        this.fileService = new FileService(fileSender);
    }

    @org.junit.Test
    public void getFileSender() {
        assertNotNull(fileService.getFileSender());
    }

    @Test
    public void uploadFile() {
        assertNotNull(fileService.getFileSender());
    }
    // TODO: Creating other unit tests using next Asserts : assertEquals, assertNull...
}