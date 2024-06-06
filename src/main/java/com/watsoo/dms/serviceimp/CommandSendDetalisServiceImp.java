package com.watsoo.dms.serviceimp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.watsoo.dms.dto.CommanddetalisSendDto;
import com.watsoo.dms.dto.DeviceInformationDto;
import com.watsoo.dms.dto.PaginatedRequestDto;
import com.watsoo.dms.dto.PaginatedResponseDto;
import com.watsoo.dms.dto.Response;
import com.watsoo.dms.entity.Command;
import com.watsoo.dms.entity.CommandSendDetails;
import com.watsoo.dms.entity.Event;
import com.watsoo.dms.repository.CommandRepository;
import com.watsoo.dms.repository.CommandSendDetalisRepository;
import com.watsoo.dms.restclient.RestClientService;
import com.watsoo.dms.service.CommandSendDetalisService;
import com.watsoo.dms.service.CommandSendTrailService;
import com.watsoo.dms.service.FileUploadDetailsService;
import com.watsoo.dms.util.Utility;

@Service
public class CommandSendDetalisServiceImp implements CommandSendDetalisService {

	@Autowired
	private CommandSendDetalisRepository commandSendDetalisRepository;

	@Autowired
	private FileUploadDetailsService fileUploadDetailsService;

	@Autowired
	private RestClientService restClientService;

	@Autowired
	private CommandRepository commandRepository;

	
	@Autowired
	private CommandSendTrailService commandSendTrailService;
	
	@Override
	public void saveCommandDetalis(List<Event> allEvent, Map<Long, String> deviceWithProtocolName) {
		List<CommandSendDetails> commandSendDetailsList = new ArrayList<>();

//		commandRepository.findBy(null, null);

		for (Event event : allEvent) {

			if (event.getEvidencePhotos() != null) {
				CommandSendDetails obj = new CommandSendDetails();

				obj.setBaseCommand("UPLOADFILE");
				obj.setCommand("UPLOADFILE," + event.getEvidencePhotos() + "#");
				obj.setEvedenceFiles(event.getEvidencePhotos());
				obj.setDeviceId(event.getDeviceId());
				obj.setEventId(event.getId());
				obj.setEventType(event.getEventType().name());
				obj.setPositionId(event.getPositionId());
				obj.setCreateOn(LocalDateTime.now());
				obj.setImeiNumber(event.getImeiNo());

				List<String> convertStringToArray = Utility.convertStringToArray(event.getEvidencePhotos());
				obj.setNoOfFileReq(convertStringToArray.size());

				commandSendDetailsList.add(obj);

			}

		}
		List<CommandSendDetails> saveAllCommandDetalis = commandSendDetalisRepository.saveAll(commandSendDetailsList);
		if (saveAllCommandDetalis != null && saveAllCommandDetalis.size() > 0) {
			fileUploadDetailsService.saveFileDetalis(saveAllCommandDetalis);

		}

	}

	@Override
	public Response<?> getAllCommandDetalis(int pageSize, int pageNo) {
		try {
			PaginatedRequestDto paginatedRequest = new PaginatedRequestDto(pageSize, pageNo);

			Pageable pageable = pageSize > 0 ? PageRequest.of(pageNo, pageSize) : Pageable.unpaged();
			Page<CommandSendDetails> findAllCommandsSendDetalis = commandSendDetalisRepository.findAll(paginatedRequest,
					pageable);

			List<CommandSendDetails> allCommands = findAllCommandsSendDetalis.getContent();
			List<CommanddetalisSendDto> commandDetailsDtoList = new ArrayList<>();
			if (allCommands != null && !allCommands.isEmpty()) {
				commandDetailsDtoList = allCommands.stream().map(CommanddetalisSendDto::entityToDto)
						.collect(Collectors.toList());

			}
			PaginatedResponseDto<Object> paginatedResponseDto = new PaginatedResponseDto<>(commandRepository.count(),
					allCommands.size(), findAllCommandsSendDetalis.getTotalPages(), pageNo, commandDetailsDtoList);
			return new Response<>("Success", paginatedResponseDto, 200);
		} catch (Exception e) {
			return new Response<>("Something Went Wrong", null, 400);

		}
	}

	@Override
	public Response<?> sendCommandManually(CommanddetalisSendDto commanddetalisSendDto) {

		if (commanddetalisSendDto == null || commanddetalisSendDto.getId() == null
				|| commanddetalisSendDto.getCommand() == null) {
			return new Response<>("Failed: Invalid format or missing critical fields", null, 400);
		}

		Optional<CommandSendDetails> findById = commandSendDetalisRepository.findById(commanddetalisSendDto.getId());
		if (findById.isPresent()) {
			CommandSendDetails commandSendDetails = findById.get();
			commandSendDetails.setCommand(commanddetalisSendDto.getCommand());

			restClientService.sendHttpPostRequestForCommand(commandSendDetails);
		}
		
		commandSendTrailService.saveManualCommand(commanddetalisSendDto);

		return new Response<>("Command Send Successfully", null, 200);

	}

	@Override
	public void sendCommand(int reCallCount, int processSleepTime) {
		List<CommandSendDetails> allCommands = commandSendDetalisRepository.findAll();

		if (allCommands != null && !allCommands.isEmpty()) {
			Set<Long> allDeviceID = allCommands.stream().map(CommandSendDetails::getDeviceId)
					.collect(Collectors.toSet());

			String deviceInformation = restClientService.getDeviceInformation(allDeviceID);
			Map<Long, DeviceInformationDto> retrieveDeviceInfoMap = new HashMap<>();
			if (deviceInformation != null && !deviceInformation.isEmpty()) {
				retrieveDeviceInfoMap = retrieveDeviceInfoMap(deviceInformation);
			}

			Map<Long, List<CommandSendDetails>> deviceIdWithMap = new HashMap<>();
			for (CommandSendDetails commandSendDetails : allCommands) {

				if (commandSendDetails.getReCallCount() == null || commandSendDetails.getReCallCount() <= reCallCount) {
					if (retrieveDeviceInfoMap != null
							&& retrieveDeviceInfoMap.get(commandSendDetails.getDeviceId()) != null && "online"
									.equals(retrieveDeviceInfoMap.get(commandSendDetails.getDeviceId()).getStatus())) {

						deviceIdWithMap.computeIfAbsent(commandSendDetails.getDeviceId(), k -> new ArrayList<>())
								.add(commandSendDetails);

					}
					Integer countRecall = commandSendDetails.getReCallCount() == null ? 0
							: commandSendDetails.getReCallCount() + 1;
					commandSendDetails.setReCallCount(countRecall);
					commandSendDetails.setReCallOn(LocalDateTime.now());

				}

			}
			commandSendDetalisRepository.saveAll(allCommands);
			processCommand(deviceIdWithMap, processSleepTime);
		}
	}

	public void processCommand(Map<Long, List<CommandSendDetails>> deviceIdWithMap, int processSleepTime) {
		
		ExecutorService executorService = Executors.newFixedThreadPool(deviceIdWithMap.size());

		CountDownLatch latch = new CountDownLatch(deviceIdWithMap.values().stream().mapToInt(List::size).sum());

		for (Map.Entry<Long, List<CommandSendDetails>> entry : deviceIdWithMap.entrySet()) {
			Long deviceId = entry.getKey();
			List<CommandSendDetails> commandList = entry.getValue();

			executorService.submit(() -> {
				for (CommandSendDetails commandSendDetails : commandList) {

					restClientService.sendHttpPostRequestForCommand(commandSendDetails);

					System.out.println(LocalDateTime.now().toLocalTime());

					try {
						Thread.sleep(processSleepTime * 1000);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					} finally {
						// Count down the latch after each command is processed
						latch.countDown();
					}
				}
			});
		}

		// Wait for all commands to complete before shutting down the executor service
		try {
			latch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		// Shutdown the executor service
		executorService.shutdown();
	}

	private Map<Long, DeviceInformationDto> retrieveDeviceInfoMap(String deviceInformation) {
		Map<Long, DeviceInformationDto> deviceInformationByDeviceId = new HashMap<>();
		try {

			Gson gson = new Gson();
			JsonArray deviceInformationJson = gson.fromJson(deviceInformation, JsonArray.class);
			for (JsonElement jsonElement : deviceInformationJson) {

				DeviceInformationDto deviceInformationDto = new DeviceInformationDto();
				JsonObject deviceInformationJsonObject = jsonElement.getAsJsonObject();
				if (deviceInformationJsonObject.has("id")) {
					long deviceId = deviceInformationJsonObject.get("id").getAsLong();

					if (deviceInformationJsonObject.has("status")) {
						String status = deviceInformationJsonObject.get("status").getAsString();
						deviceInformationDto.setStatus(status);
						deviceInformationByDeviceId.put(deviceId, deviceInformationDto);

					}

				}

			}

		} catch (Exception e) {

		}
		return deviceInformationByDeviceId;
	}

}
