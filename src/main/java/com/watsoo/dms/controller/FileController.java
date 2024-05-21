package com.watsoo.dms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.watsoo.dms.dto.Response;
import com.watsoo.dms.service.FileService;


@RestController
public class FileController {
	
	@Autowired
	private FileService fileService;
	
	
	@PostMapping("/v1/uploadFile")
	public ResponseEntity<?> uploadFileInLocalAndResponseAsDownloadUrl(@ModelAttribute MultipartFile file)
			throws Exception {

		Response<?> respone = fileService.storeFileInLocalDirectoryResponseIsDownloadUrl(file,
				System.currentTimeMillis());

		return new ResponseEntity<>(respone, HttpStatus.valueOf(respone.getResponseCode()));

	}
	
	@GetMapping("/getFile/{filename}")
	public ResponseEntity<?> getFile(@PathVariable("filename") String filename) {

		Resource resource = fileService.downloadDocument(filename);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);

	}

}
