package com.watsoo.dms.serviceimp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.watsoo.dms.entity.CommandSendDetails;
import com.watsoo.dms.entity.FileUploadDetails;
import com.watsoo.dms.repository.FileUploadDetailsRepository;
import com.watsoo.dms.restclient.RestClientService;
import com.watsoo.dms.service.FileUploadDetailsService;
import com.watsoo.dms.util.Utility;

@Service
public class FileUploadDetailsServiceImp implements FileUploadDetailsService {

	@Autowired
	private FileUploadDetailsRepository fileUploadDetailsRepository;

	@Value("${file.dawnload.url}")
	String fileDawnloadUrl;

	@Autowired
	private RestClientService restClientService;

	@Override
	public void saveFileDetalis(List<CommandSendDetails> allCommandDetalis) {
		List<FileUploadDetails> fileUploadList = new ArrayList<>();

		for (CommandSendDetails commandSendDetails : allCommandDetalis) {

			List<String> convertStringToArray = Utility.convertStringToArray(commandSendDetails.getEvedenceFiles());

			for (String fileName : convertStringToArray) {
				FileUploadDetails obj = new FileUploadDetails();
				obj.setCommandSendId(commandSendDetails.getId());
				obj.setFileDownloadUrl(fileDawnloadUrl);
				obj.setFileName(fileName);
				String filePresentOrNot = restClientService.getFilePresentOrNot(fileName);
				boolean isFileExit = false;
				if (filePresentOrNot != null) {

					Gson gson = new Gson();
					JsonObject resposnse = gson.fromJson(filePresentOrNot, JsonObject.class);
					if (resposnse.has("code")) {
						String responseCode = resposnse.get("code").getAsString();
						if (responseCode.equals("200")) {
							isFileExit = true;
						}
					}

				}
				obj.setIsFileExist(isFileExit);
				fileUploadList.add(obj);

			}

		}
		if (fileUploadList != null && fileUploadList.size() > 0) {
			List<FileUploadDetails> saveAll = fileUploadDetailsRepository.saveAll(fileUploadList);
		}
	}

}
