package com.watsoo.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.watsoo.dms.entity.FileUploadDetails;

public interface FileUploadDetailsRepository extends JpaRepository<FileUploadDetails, Long> {

}
