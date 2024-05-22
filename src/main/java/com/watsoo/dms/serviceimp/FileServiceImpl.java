package com.watsoo.dms.serviceimp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.watsoo.dms.dto.Response;
import com.watsoo.dms.service.FileService;

@Service
public class FileServiceImpl implements FileService {

	private Path dirLocation;

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
			String serverIpAddress = InetAddress.getLoopbackAddress().getHostAddress();
			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
	                .host(serverIpAddress)
	                .path("/getFile/")
	                .path(fileName)
	                .toUriString();
			return new Response<>("File successfull uploaded", fileDownloadUri, HttpStatus.OK.value());
		} catch (IOException ex) {
			return new Response<>("File fail to uploaded", null, HttpStatus.BAD_REQUEST.value());
		}
	}

	@Override
	public Resource downloadDocument(String fileName) {

		try {

			Path file = this.dirLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not find file");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Could not download file");
		}

	}

}
