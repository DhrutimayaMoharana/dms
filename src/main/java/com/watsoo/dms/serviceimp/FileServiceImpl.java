package com.watsoo.dms.serviceimp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.watsoo.dms.dto.Response;
import com.watsoo.dms.restclient.RestClientService;
import com.watsoo.dms.service.FileService;

@Service
public class FileServiceImpl implements FileService {

	private Path dirLocation;
	
	@Autowired
	private RestClientService restClientService;

	public FileServiceImpl(@Value("${file.upload.dir}") String directory) {
		this.dirLocation = Paths.get(directory).toAbsolutePath().normalize();
		;
	}

	@Override
	public Response<?> storeFileInLocalDirectoryResponseIsDownloadUrl(MultipartFile file, Long currentDate) {
		String fileName = StringUtils.cleanPath(currentDate + file.getOriginalFilename());

		try {
			Path targetLocation = this.dirLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/getFile/")
					.path(fileName).toUriString();
			return new Response<>("File successfull uploaded", fileDownloadUri, HttpStatus.OK.value());
		} catch (IOException ex) {
			return new Response<>("File fail to uploaded", null, HttpStatus.BAD_REQUEST.value());
		}
	}

	@Override
	public Response<?> downloadDocument(String fileName) {

		try {

			
			String checkFile = restClientService.getFile(fileName);
			byte[] byteArray = checkFile.getBytes();
            Resource resource = new ByteArrayResource(byteArray);
            
			if (resource.exists() || resource.isReadable()) {
				return new Response<>("Image featch ",resource,200);
			} else {
				throw new RuntimeException("Could not find file");
			}
		} catch (Exception e) {
			throw new RuntimeException("Could not download file");
		}

	}

}
