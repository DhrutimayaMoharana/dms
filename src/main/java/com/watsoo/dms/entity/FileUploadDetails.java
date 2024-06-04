package com.watsoo.dms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "file_upload_details")
public class FileUploadDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String fileName;

	private Long commandSendId;

	private String fileDownloadUrl;

	private Boolean isFileExist;

	private Double fileSize;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getCommandSendId() {
		return commandSendId;
	}

	public void setCommandSendId(Long commandSendId) {
		this.commandSendId = commandSendId;
	}

	public String getFileDownloadUrl() {
		return fileDownloadUrl;
	}

	public void setFileDownloadUrl(String fileDownloadUrl) {
		this.fileDownloadUrl = fileDownloadUrl;
	}

	public Boolean getIsFileExist() {
		return isFileExist;
	}

	public void setIsFileExist(Boolean isFileExist) {
		this.isFileExist = isFileExist;
	}

	public Double getFileSize() {
		return fileSize;
	}

	public void setFileSize(Double fileSize) {
		this.fileSize = fileSize;
	}

}
