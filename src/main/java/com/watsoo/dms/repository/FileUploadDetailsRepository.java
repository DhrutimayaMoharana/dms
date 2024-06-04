package com.watsoo.dms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.watsoo.dms.entity.FileUploadDetails;

public interface FileUploadDetailsRepository extends JpaRepository<FileUploadDetails, Long> {
	
	List<FileUploadDetails> findByIsFileExistIsNullOrIsFileExistFalse();
	}


