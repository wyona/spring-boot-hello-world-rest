package org.wyona.webapp.models;

import java.io.IOException;
import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

public class Attachment implements Serializable {

	private static final long serialVersionUID = 1L;

	private MultipartFile file;

	public Attachment() {
	}

	public Attachment(MultipartFile file) throws IOException {
		super();
		this.setFile(file);
	}

	public String getFilename() {
		return file.getOriginalFilename();
	}

	public byte[] getBdata() throws IOException {
		return file.getBytes();
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

}
