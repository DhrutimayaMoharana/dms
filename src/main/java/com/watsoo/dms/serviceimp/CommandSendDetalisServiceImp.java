package com.watsoo.dms.serviceimp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.watsoo.dms.entity.CommandSendDetails;
import com.watsoo.dms.entity.Event;
import com.watsoo.dms.repository.CommandSendDetalisRepository;
import com.watsoo.dms.service.CommandSendDetalisService;
import com.watsoo.dms.service.FileUploadDetailsService;
import com.watsoo.dms.util.Utility;

@Service
public class CommandSendDetalisServiceImp implements CommandSendDetalisService {

	@Autowired
	private CommandSendDetalisRepository commandSendDetalisRepository;
	
	@Autowired
	private FileUploadDetailsService fileUploadDetailsService;

	@Override
	public void saveCommandDetalis(List<Event> allEvent) {
		List<CommandSendDetails> commandSendDetailsList = new ArrayList<>();

		for (Event event : allEvent) {

			if (event.getEvidencePhotos() != null) {
				CommandSendDetails obj = new CommandSendDetails();

				obj.setBaseCommand("UPLOADFILE");
				obj.setCommand("UPLOADFILE," + event.getEvidencePhotos());
				obj.setEvedenceFiles(event.getEvidencePhotos());
				obj.setDeviceId(event.getDeviceId());
				obj.setEventId(event.getId());
				obj.setEventType(event.getEventType().name());
				obj.setPositionId(event.getPositionId());
				obj.setCreateOn(LocalDateTime.now());
				obj.setImeiNumber(event.getImeiNo());

				List<String> convertStringToArray =Utility.convertStringToArray(event.getEvidencePhotos());
				obj.setNoOfFileReq(convertStringToArray.size());

				commandSendDetailsList.add(obj);

			}
			List<CommandSendDetails> saveAllCommandDetalis = commandSendDetalisRepository.saveAll(commandSendDetailsList);
			if(saveAllCommandDetalis!=null && saveAllCommandDetalis.size()>0) {
				fileUploadDetailsService.saveFileDetalis(saveAllCommandDetalis);
				
				
				
			}
		}

	}



}
