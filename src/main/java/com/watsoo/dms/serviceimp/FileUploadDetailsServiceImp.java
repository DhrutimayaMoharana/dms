package com.watsoo.dms.serviceimp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.watsoo.dms.entity.CommandSendDetails;
import com.watsoo.dms.entity.FileUploadDetails;
import com.watsoo.dms.enums.CommandStatus;
import com.watsoo.dms.repository.CommandSendDetalisRepository;
import com.watsoo.dms.repository.FileUploadDetailsRepository;
import com.watsoo.dms.restclient.RestClientService;
import com.watsoo.dms.service.FileUploadDetailsService;
import com.watsoo.dms.util.Utility;

@Service
public class FileUploadDetailsServiceImp implements FileUploadDetailsService {

	@Autowired
	private FileUploadDetailsRepository fileUploadDetailsRepository;

	@Autowired
	private CommandSendDetalisRepository commandSendDetalisRepository;

	@Value("${file.dawnload.url}")
	String fileDawnloadUrl;

	@Autowired
	private RestClientService restClientService;

	@Override
	public void saveFileDetalis(List<CommandSendDetails> allCommandDetalis) {
		List<FileUploadDetails> fileUploadList = new ArrayList<>();

		for (CommandSendDetails commandSendDetails : allCommandDetalis) {

			List<String> convertStringToArray = Utility.convertStringToArray(commandSendDetails.getEvedenceFiles());

			if (commandSendDetails.getReCallCount() == null || commandSendDetails.getReCallCount() == 0) {
				for (String fileName : convertStringToArray) {
					FileUploadDetails obj = new FileUploadDetails();
					obj.setCommandSendId(commandSendDetails.getId());
					obj.setFileDownloadUrl(fileDawnloadUrl);
					obj.setFileName(fileName);
					obj.setCreatedOn(new Date());
					obj.setUpdatedOn(new Date());
					boolean isFileExit = false;
					obj.setIsFileExist(isFileExit);
					fileUploadList.add(obj);

				}
			}
		}
		if (fileUploadList != null && fileUploadList.size() > 0) {
			List<FileUploadDetails> saveAll = fileUploadDetailsRepository.saveAll(fileUploadList);
		}
	}

	@Override
	public void updateFlleDetalis(int reCallCount) {

		try {

			List<FileUploadDetails> findByIsFileExistIsNullOrIsFileExistFalse = fileUploadDetailsRepository
					.findByStatusIsNullOrStatusIsPartiallySuccess();
//					
//					.findByIsFileExistIsNullOrIsFileExistFalse();

			if (findByIsFileExistIsNullOrIsFileExistFalse != null
					&& findByIsFileExistIsNullOrIsFileExistFalse.size() > 0) {

				Set<Long> commandIds = findByIsFileExistIsNullOrIsFileExistFalse.stream()
						.map(FileUploadDetails::getCommandSendId).collect(Collectors.toSet());

				List<CommandSendDetails> commandDetalisByCommandId = commandSendDetalisRepository
						.findAllByIdIn(commandIds);

				Map<Long, CommandSendDetails> commandDetalisMapWIthId = commandDetalisByCommandId.stream()
						.collect(Collectors.toMap(CommandSendDetails::getId, detail -> detail));

				for (FileUploadDetails fileDetails : findByIsFileExistIsNullOrIsFileExistFalse) {

					boolean isFileExit = false;

					if (fileDetails.getReCallCount() == null
							|| fileDetails.getReCallCount().intValue() <= reCallCount) {

						String filePresentOrNot = restClientService.getFilePresentOrNot(fileDetails.getFileName());
						if (filePresentOrNot != null) {
							Gson gson = new Gson();
							JsonObject resposnse = gson.fromJson(filePresentOrNot, JsonObject.class);
							if (resposnse != null && resposnse.has("code")) {
								String responseCode = resposnse.get("code").getAsString();
								if (responseCode.equals("200")) {
									if (resposnse.has("data")) {

										

										JsonObject data = resposnse.get("data").getAsJsonObject();
										if (data.has("fileSize")) {
											fileDetails.setFileSize(data.get("fileSize").getAsDouble());

										}
									}

									CommandSendDetails commandSendDetails = commandDetalisMapWIthId
											.get(fileDetails.getCommandSendId());
									commandSendDetails
											.setNoOfFileUploaded(commandSendDetails.getNoOfFileUploaded() != null
													? (commandSendDetails.getNoOfFileUploaded() + 1)
													: 0 + 1);
									commandSendDetails.setUpdatedOn(LocalDateTime.now());
									commandDetalisMapWIthId.put(fileDetails.getCommandSendId(), commandSendDetails);

									if (commandSendDetails.getNoOfFileUploaded().intValue() == commandSendDetails
											.getNoOfFileReq().intValue()) {

										commandSendDetails.setStatus(CommandStatus.COMPLETE_SUCCESS);
									} else {
										commandSendDetails.setStatus(CommandStatus.PARTIALY_SUCCESS);
									}

									fileDetails.setStatus(CommandStatus.COMPLETE_SUCCESS.name());

									isFileExit = true;
								}
							}
						}
					} else {
						fileDetails.setStatus(CommandStatus.FAILED.name());
					}

					fileDetails.setReCallCount(fileDetails.getReCallCount() != null
							? (fileDetails.getReCallCount() + 1)
							: 0 + 1);
					fileDetails.setIsFileExist(isFileExit);
					fileDetails.setUpdatedOn(new Date());

				}
				fileUploadDetailsRepository.saveAll(findByIsFileExistIsNullOrIsFileExistFalse);
				if (commandDetalisMapWIthId != null && commandDetalisMapWIthId.values() != null) {
					commandSendDetalisRepository.saveAll(commandDetalisMapWIthId.values());

				}
			}

		} catch (

		Exception e) {
			e.printStackTrace();
		}

	}

}
