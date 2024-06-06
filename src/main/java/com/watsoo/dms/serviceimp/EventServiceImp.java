package com.watsoo.dms.serviceimp;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.watsoo.dms.constant.Constant;
import com.watsoo.dms.dto.DeviceInformationDto;
import com.watsoo.dms.dto.EventDto;
import com.watsoo.dms.dto.EventTypeCountDto;
import com.watsoo.dms.dto.PaginatedRequestDto;
import com.watsoo.dms.dto.PaginatedResponseDto;
import com.watsoo.dms.dto.Response;
import com.watsoo.dms.entity.Configuration;
import com.watsoo.dms.entity.Event;
import com.watsoo.dms.entity.Vehicle;
import com.watsoo.dms.enums.EventType;
import com.watsoo.dms.repository.ConfigurationRepository;
import com.watsoo.dms.repository.DriverRepository;
import com.watsoo.dms.repository.EventRepository;
import com.watsoo.dms.repository.RemarkRepository;
import com.watsoo.dms.repository.VehicleRepository;
import com.watsoo.dms.restclient.RestClientService;
import com.watsoo.dms.service.CommandSendDetalisService;
import com.watsoo.dms.service.EventService;
import com.watsoo.dms.util.Month;
import com.watsoo.dms.util.TimeUtility;

@Service
public class EventServiceImp implements EventService {

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private RestClientService restClientService;

	@Autowired
	private DriverRepository driverRepository;

	@Autowired
	private ConfigurationRepository configurationRepository;

	@Autowired
	private RemarkRepository remarkRepository;

	@Autowired
	private CommandSendDetalisService commandSendDetalisService;

	@Value("${file.get.url}")
	String fileUrl;

//	@Override
//	public Response<?> getAllEvent(int pageSize, int pageNo, String vehicleNo, String driverName, String eventType,
//			String searchKey) {
//
//		try {
//			PaginatedRequestDto paginatedRequest = new PaginatedRequestDto(pageSize, pageNo, vehicleNo, driverName,
//					eventType, searchKey);
//			Pageable pageable = pageSize > 0 ? PageRequest.of(pageNo, pageSize) : Pageable.unpaged();
//			Page<Event> findAllEvent = eventRepository.findAll(paginatedRequest, pageable);
//			List<Event> events = findAllEvent.getContent();
//
//			Set<Long> allDeviceID = events.stream().map(event -> event.getDeviceId()).collect(Collectors.toSet());
//
//			Map<Integer, Vehicle> vehiclesByDeviceId = new HashMap<>();
//			Map<Long, Driver> allDriver = new HashMap<>();
//			List<Long> vechileIdList = new ArrayList<>();
//
//			if (allDeviceID != null && !allDeviceID.isEmpty()) {
//				List<Vehicle> vehicles = vehicleRepository.findVehiclesByDeviceIds(allDeviceID);
//
//				vehicles = checkImeiNumberIsPresent(vehicles, allDeviceID);
//
//				vehiclesByDeviceId = vehicles.stream()
//						.collect(Collectors.toMap(Vehicle::getDeviceId, Function.identity()));
//
//				vechileIdList = vehicles.stream().map(Vehicle::getId).collect(Collectors.toList());
//
//				List<Driver> allDrivers = driverRepository.findAllById(vechileIdList);
//				allDriver = allDrivers.stream().collect(Collectors.toMap(Driver::getVehicle_id, Function.identity()));
//			}
//
//			List<EventDto> eventsdto = new ArrayList<>();
//
//			// Iterate over the events list
//			for (Event event : events) {
//				EventDto fromEventToEventDto = EventDto.fromEntity(event);
//				if (event.getEvidencePhotos() != null) {
//					fromEventToEventDto.setEvidencePhotos(convertStringToArray(event.getEvidencePhotos()));
//				}
//				Long deviceId = event.getDeviceId();
//				Vehicle vehicle = vehiclesByDeviceId.get(deviceId.intValue()); // Convert Long to Integer
//				fromEventToEventDto.setVehicleDto(VehicleDto.fromEntity(vehicle));
//				Driver driver = allDriver.get(vehicle.getId());
//				fromEventToEventDto.setDriverDto(DriverDto.convertEntityToDto(driver));
//
//				eventsdto.add(fromEventToEventDto);
//			}
//
//			PaginatedResponseDto<Object> paginatedResponseDto = new PaginatedResponseDto<>(eventRepository.count(),
//					events.size(), findAllEvent.getTotalPages(), pageNo, eventsdto);
//			return new Response<>("Event List", paginatedResponseDto, HttpStatus.OK.value());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new Response<>("Error occurred while processing Event list", null, HttpStatus.BAD_REQUEST.value());
//
//		}
//
//	}

	@Override
	public Response<?> getAllEvent(int pageSize, int pageNo, String vehicleNo, String driverName, String eventType,
			String searchKey, String fromDate, String toDate, String dlNumber) {

		try {
			PaginatedRequestDto paginatedRequest = new PaginatedRequestDto(pageSize, pageNo, vehicleNo, driverName,
					eventType, searchKey, fromDate, toDate, dlNumber);
			Pageable pageable = pageSize > 0 ? PageRequest.of(pageNo, pageSize) : Pageable.unpaged();
			Page<Event> findAllEvent = eventRepository.findAll(paginatedRequest, pageable);
			List<Event> events = findAllEvent.getContent();

			Set<Long> allDeviceID = events.stream().map(event -> event.getDeviceId()).collect(Collectors.toSet());
//
			String deviceInformation = restClientService.getDeviceInformation(allDeviceID);

			int totalActiveVehicle = 0;
			int totalInActiveVehicle = 0;

			Map<Long, DeviceInformationDto> retrieveDeviceInfoMap = new HashMap<>();
			if (deviceInformation != null && !deviceInformation.equals("")) {
				retrieveDeviceInfoMap = retrieveDeviceInfoMap(deviceInformation);

				List<DeviceInformationDto> listOfDeviceInformation = new ArrayList<>(retrieveDeviceInfoMap.values());
				for (DeviceInformationDto deviceInformationDto : listOfDeviceInformation) {
					if (deviceInformationDto.getStatus().equals("online")) {
						totalActiveVehicle++;
					} else {
						totalInActiveVehicle++;
					}

				}

			}

			// all events for Dashboard
			List<Event> allEvents = eventRepository.findAll();
			EventTypeCountDto eventsAllDetalisForDashBoard = getEventsAllDetalisForDashBoard(allEvents, allDeviceID);
			eventsAllDetalisForDashBoard.setTotalInActiveVehicle(totalInActiveVehicle);
			eventsAllDetalisForDashBoard.setTotalActiveVehicle(totalActiveVehicle);

			List<EventDto> eventsdto = new ArrayList<>();

			for (Event event : events) {
				EventDto fromEventToEventDto = EventDto.fromEntity(event);
				if (event.getEvidencePhotos() != null) {
					fromEventToEventDto.setEvidencePhotos(convertStringToArray(event.getEvidencePhotos()));
				}

//				if (retrieveDeviceInfoMap != null) {
//
//					fromEventToEventDto.setDeviceInformationDto(retrieveDeviceInfoMap.get(event.getDeviceId()));
//				}

				if (!event.getEventType().equals(EventType.POWER_CUT)) {
					eventsdto.add(fromEventToEventDto);
				}
			}

			PaginatedResponseDto<Object> paginatedResponseDto = new PaginatedResponseDto<>(eventRepository.count(),
					events.size(), findAllEvent.getTotalPages(), pageNo, eventsdto, eventsAllDetalisForDashBoard);
			return new Response<>("Event List", paginatedResponseDto, HttpStatus.OK.value());

		} catch (Exception e) {
			e.printStackTrace();
			return new Response<>("Error occurred while processing Event list", null, HttpStatus.BAD_REQUEST.value());

		}

	}

	private EventTypeCountDto getEventsAllDetalisForDashBoard(List<Event> allEvents, Set<Long> allDeviceID) {

		int numberOfDays = 4;
		Optional<Configuration> findByKey = configurationRepository.findByKey("DEFAULTER_DRIVER");
		if (findByKey.isPresent() && findByKey.get() != null) {
			numberOfDays = Integer.valueOf(findByKey.get().getValue());
		}

		int countDefaulterDriver = 0;
		long yawningCount = 0;
		long mobileUsageCount = 0;
		long distractionCount = 0;
		long smokingCount = 0;
		long closeEyesCount = 0;
		long noFaceCount = 0;
		long lowHeadCount = 0;
		long drinkingCount = 0;
		long totalEventCount = 0;
		int tamperedDevices = 0;

		Map<String, List<Event>> categorizeEventsByDlNo = categorizeEventsByDlNo(allEvents);
		for (String dlNumber : categorizeEventsByDlNo.keySet()) {

			List<Event> list = categorizeEventsByDlNo.get(dlNumber);
			if (list.size() >= numberOfDays) {
				countDefaulterDriver++;
			}

		}
		Map<Long, Event> lastEventByDevice = getLastEventByDevice(allEvents, allDeviceID);

		Map<String, EventDto> eventAndDriverPerfomance = new HashMap<>();
		for (Long x : lastEventByDevice.keySet()) {
			Event event = lastEventByDevice.get(x);

			if (event.getEventType().name().equals(EventType.POWER_CUT.name())) {
				tamperedDevices++;
			}

		}

		for (Event event : allEvents) {

			EventType eventType = event.getEventType();
			if (eventType != null) {
				switch (eventType) {
				case YAWN_ALERT -> yawningCount++;
				case PHONE_CALLING -> mobileUsageCount++;
				case DISTRACTION -> distractionCount++;
				case SMOKING_ALERT -> smokingCount++;
				case CLOSE_EYES -> closeEyesCount++;
				case NO_FACE -> noFaceCount++;
				case LOW_HEAD -> lowHeadCount++;
				case DRINKING -> drinkingCount++;
				default -> {
				}
				}

			}

		}
		EventTypeCountDto eventTypeCountDto = new EventTypeCountDto(yawningCount, mobileUsageCount, distractionCount,
				smokingCount, closeEyesCount, noFaceCount, lowHeadCount, drinkingCount, allEvents.size());
		eventTypeCountDto.setTamperedDevices(tamperedDevices);
		eventTypeCountDto.setCountDefaulterDriver(countDefaulterDriver);
		return eventTypeCountDto;
	}

	public static Map<Long, Event> getLastEventByDevice(List<Event> allEvents, Set<Long> allDeviceIDs) {
		Map<Long, Event> lastEventsByDevice = new HashMap<>();

		// Filter events by device IDs and group by device ID
		Map<Long, List<Event>> eventsByDevice = allEvents.stream()
				.filter(event -> allDeviceIDs.contains(event.getDeviceId()))
				.collect(Collectors.groupingBy(Event::getDeviceId));

		// Find the last added event for each device ID
		for (Map.Entry<Long, List<Event>> entry : eventsByDevice.entrySet()) {
			List<Event> events = entry.getValue();
			events.sort(Comparator.comparing(Event::getEventServerCreateTime));
			lastEventsByDevice.put(entry.getKey(), events.get(events.size() - 1));
		}

		return lastEventsByDevice;
	}

	public static Map<String, List<Event>> categorizeEventsByDlNo(List<Event> allEvents) {
		Map<String, List<Event>> eventsByDlNo = new HashMap<>();

		for (Event event : allEvents) {
			String dlNo = event.getDlNo();
			eventsByDlNo.computeIfAbsent(dlNo, k -> new ArrayList<>()).add(event);
		}

		return eventsByDlNo;
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

	@Override
	public Response<?> getEventById(Long eventId) {
		EventDto dto = new EventDto();
		Optional<Event> eventById = eventRepository.findById(eventId);
		if (eventById.isPresent() && eventById.get() != null) {
			Event event = eventById.get();
			dto = EventDto.fromEntity(event);

			if (event.getEvidencePhotos() != null) {
				dto.setEvidencePhotos(convertStringToArray(event.getEvidencePhotos()));
			}

		}
		return new Response<>("Event fetched successfully", dto, 200);

	}

	private List<String> convertStringToArray(String evidencePhotos) {
		String[] urlArray = evidencePhotos.split(",");
		List<String> fullUrls = new ArrayList<>();

		for (String url : urlArray) {
			fullUrls.add(fileUrl + url);
		}

		return fullUrls;
	}

	@Override
	public Response<EventTypeCountDto> fetchDashBoardCounts(String value) {

//		Optional<CredentialMaster> master = userDetailsService.getUserDetails();

		List<Event> eventList = new ArrayList<>();

		try {
			if (value != null && !value.equals("")) {
				String fromDate = "";
				String toDate = "";
				String addedFromTime = "00:00:00";
				String addedToTime = "23:59:59";
				if (value.equals(Constant.TODAY)) {
					Date todayDate = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					String formattedDate = formatter.format(todayDate);
					fromDate = formattedDate + " " + addedFromTime;
					toDate = formattedDate + " " + addedToTime;
				} else if (value.equals(Constant.YESTERDAY)) {
					Calendar today = Calendar.getInstance();
					Calendar yesterday = (Calendar) today.clone();
					yesterday.add(Calendar.DAY_OF_MONTH, -1);
					Date utilYesterday = yesterday.getTime();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					String formattedDate = formatter.format(utilYesterday);
					fromDate = formattedDate + " " + addedFromTime;
					toDate = formattedDate + " " + addedToTime;
				} else if (value.equals(Constant.THIS_MONTH)) {
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.DAY_OF_MONTH, 1);
					Date startDate = calendar.getTime();
					calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
					Date endDate = calendar.getTime();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					String formattedStartDate = dateFormat.format(startDate);
					String formattedEndDate = dateFormat.format(endDate);
					fromDate = formattedStartDate + " " + addedFromTime;
					toDate = formattedEndDate + " " + addedToTime;
				} else if (value.equals(Constant.THIS_WEEK)) {
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
					Date startDate = calendar.getTime();
					calendar.add(Calendar.DAY_OF_WEEK, 6);
					Date endDate = calendar.getTime();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					String formattedStartDate = dateFormat.format(startDate);
					String formattedEndDate = dateFormat.format(endDate);
					fromDate = formattedStartDate + " " + addedFromTime;
					toDate = formattedEndDate + " " + addedToTime;
					System.out.println(fromDate);
					System.out.println(toDate);
				} else if (value.equals(Constant.LAST_MONTH)) {
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.DAY_OF_MONTH, 1);
					calendar.add(Calendar.MONTH, -1);
					Date lastMonthStartDate = calendar.getTime();
					calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
					Date lastMonthEndDate = calendar.getTime();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					String formattedLastMonthStartDate = dateFormat.format(lastMonthStartDate);
					String formattedLastMonthEndDate = dateFormat.format(lastMonthEndDate);
					fromDate = formattedLastMonthStartDate + " " + addedFromTime;
					toDate = formattedLastMonthEndDate + " " + addedToTime;
				}

				else {
					Date todayDate = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					String formattedDate = formatter.format(todayDate);
					fromDate = formattedDate + " " + addedFromTime;
					toDate = formattedDate + " " + addedToTime;
				}

				if (value.equals(Constant.TILL_TODAY)) {
					eventList = eventRepository.findAll();
				} else {
					eventList = eventRepository.findEventsBetweenDates(fromDate, toDate);
				}
			} else {
				eventList = eventRepository.findAll();
			}

			long yawningCount = 0;
			long mobileUsageCount = 0;
			long distractionCount = 0;
			long smokingCount = 0;
			long closeEyesCount = 0;
			long noFaceCount = 0;
			long lowHeadCount = 0;
			long drinkingCount = 0;
			long totalEventCount = 0;
			int totalActiveVehicle = 0;
			int totalInActiveVehicle = 0;

			if (eventList != null && eventList.size() > 0) {
				totalEventCount = eventList.size();
				for (Event event : eventList) {
					EventType eventType = event.getEventType();
					if (eventType != null) {

						switch (eventType) {
						case YAWN_ALERT -> yawningCount++;
						case PHONE_CALLING -> mobileUsageCount++;
						case DISTRACTION -> distractionCount++;
						case SMOKING_ALERT -> smokingCount++;
						case CLOSE_EYES -> closeEyesCount++;
						case NO_FACE -> noFaceCount++;
						case LOW_HEAD -> lowHeadCount++;
						case DRINKING -> drinkingCount++;
						default -> {
						}
						}
					}
				}
			}

			EventTypeCountDto eventTypeCountDto = new EventTypeCountDto(yawningCount, mobileUsageCount,
					distractionCount, smokingCount, closeEyesCount, noFaceCount, lowHeadCount, drinkingCount,
					totalEventCount);
			eventTypeCountDto.setTotalActiveVehicle(totalActiveVehicle);
			eventTypeCountDto.setTotalInActiveVehicle(totalInActiveVehicle);

			return new Response<>("Events Counts fetched successfully", eventTypeCountDto, 200);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response<>("Something Went Wrong", null, 400);

		}
	}

	@Override
	public Response<?> getAllEventType() {
//		List<EventType> eventTypeList = Arrays.asList(EventType.values());
		List<EventType> eventTypeList = Stream.of(EventType.values()).collect(Collectors.toList());
		return new Response<>("All event types fetched successfully", eventTypeList, 200);
	}

	@Override
	public void saveEvent(String events, List<Vehicle> vehicles) {

		Map<Integer, Vehicle> vechileMapByDeviceId = vehicles.stream()
				.collect(Collectors.toMap(Vehicle::getDeviceId, Function.identity()));

		Gson gson = new Gson();
		JsonArray eventList = gson.fromJson(events, JsonArray.class);

		Map<Long, Event> eventsDataMap = new HashMap<>();
		List<Long> positionIdList = new ArrayList<>();

		for (JsonElement eventElement : eventList) {
			JsonObject event = eventElement.getAsJsonObject();

			if (event.has("attributes")) {
				JsonObject eventAttributes = event.get("attributes").getAsJsonObject();
				if (eventAttributes.has("alarm")) {
					String eventAlarm = eventAttributes.get("alarm").getAsString();

					EventType eventType = EventType.fromType(eventAlarm);
					if (eventType != null) {
						Event eventObject = new Event();
						eventObject.setEventType(eventType);
						eventObject.setEventTime(new Date());

						eventObject.setEventServerCreateTime(
								TimeUtility.getTimeStringToDateFormat(event.get("eventTime").getAsString()));
						Long deviceId = event.get("deviceId").getAsLong();
						Long positionId = event.get("positionId").getAsLong();
						eventObject.setDeviceId(event.get("deviceId").getAsLong());
						eventObject.setPositionId(positionId);
						eventObject.setEventId(event.get("id").getAsLong());
						// Its update When Vechile Service call
						// -->
						eventObject.setDriverName("Virat");
						eventObject.setDriverPhone("5343435353");
						eventObject.setChassisNumber("4353537387383");
						eventObject.setVehicleNo("OODD44DD");
						eventObject.setImeiNo("23456789");
						eventObject.setRemark("PENDING");

						eventObject.setDlNo(new Random().ints(12, 0, 62)
								.mapToObj(
										i -> "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(i))
								.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString());
						// <--
						positionIdList.add(positionId);
						eventsDataMap.put(positionId, eventObject);
					}

				}

			}

		}

		Map<Long, String> deviceWithProtocolName = new HashMap<>();
		String positions = "";
		if (positionIdList != null && positionIdList.size() > 0) {
			positions = restClientService.getPositions(positionIdList);
			if (positions != null) {
				JsonArray positionObject = gson.fromJson(positions, JsonArray.class);
				for (JsonElement positionElement : positionObject) {
					JsonObject positionData = positionElement.getAsJsonObject();
					String evidenceFiles = "";
					if (positionData.has("id")) {

						Long positionId = positionData.get("id").getAsLong();
						Double latitude = positionData.get("latitude").getAsDouble();
						Double longitude = positionData.get("longitude").getAsDouble();

						if (positionData.has("attributes")) {
							JsonObject positionAttributes = positionData.get("attributes").getAsJsonObject();
							if (positionAttributes.has("evidenceFiles")) {
								evidenceFiles = positionAttributes.get("evidenceFiles").getAsString();
								Event event = eventsDataMap.get(positionId);
								event.setLatitude(latitude);
								event.setLongitude(longitude);
								event.setEvidencePhotos(evidenceFiles);
								eventsDataMap.put(positionId, event);

								if (positionData.has("protocol")) {
									deviceWithProtocolName.put(event.getDeviceId(),
											positionData.get("protocol").getAsString());

								}

							}

						}
					}

				}

			}

		}

		if (eventsDataMap != null && !eventsDataMap.isEmpty()) {

			List<Event> values = new ArrayList<>(eventsDataMap.values());
			Collections.sort(values, new Comparator<Event>() {
				@Override
				public int compare(Event e1, Event e2) {
					return e1.getEventServerCreateTime().compareTo(e2.getEventServerCreateTime());
				}
			});

			List<Event> saveAll = eventRepository.saveAll(values);

			if (saveAll != null && saveAll.size() > 0) {
				commandSendDetalisService.saveCommandDetalis(saveAll, deviceWithProtocolName);

			}

		}

	}

	@Override
	public Response<?> updateEvent(EventDto eventDto) {

		if (eventDto == null || eventDto.getId() == null) {

			return new Response<>("Provide Valid Event Id", null, 400);

		}

		Optional<Event> eventFindById = eventRepository.findById(eventDto.getId());
		if (eventFindById.isPresent() && eventFindById.get() != null) {
			Event event = eventFindById.get();
			if (eventDto.getRemark() != null) {
				event.setRemark(eventDto.getRemark());
				eventRepository.save(event);

			}

		}

		return new Response<>("Event Update Successfully", null, 200);
	}

	@Override
	public Response<?> getEventDetalisForDashBoard() {

		return null;
	}

	@Override
	public Response<?> getEventDetalisForDriverPerfomance(String value) {
		String fromDate = "";
		String toDate = "";
		String addedFromTime = "00:00:00";
		String addedToTime = "23:59:59";

		String fromPriviosDateTime = "";
		String toPriviosDateTime = "";

		String fromCurrentDateTime = "";
		String toThisCurrentDateTime = "";

		if (value == null) {
			return new Response<>("Value Must Be required", null, 400);
		}

		try {
			if (value.equals(Constant.LAST_TWO_MONTHS)) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MONTH, -1);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				Date previousMonthStartDate = calendar.getTime();
				calendar = Calendar.getInstance();
				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				Date currentMonthEndDate = calendar.getTime();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String formattedPreviousMonthStartDate = dateFormat.format(previousMonthStartDate);
				String formattedCurrentMonthEndDate = dateFormat.format(currentMonthEndDate);
				fromDate = formattedPreviousMonthStartDate + " " + addedFromTime;
				toDate = formattedCurrentMonthEndDate + " " + addedToTime;
			} else if (value.equals(Constant.LAST_TWO_WEEK)) {

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

				// Calculate date range for last two weeks
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.WEEK_OF_YEAR, -1); // Move to last week
				calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()); // Move to first day of last week
				Date previousWeekStartDate = calendar.getTime();
				calendar.add(Calendar.DAY_OF_WEEK, 6); // Move to last day of last week
				Date previousWeekEndDate = calendar.getTime();
				calendar.add(Calendar.DAY_OF_WEEK, 1); // Move to first day of current week
				Date currentWeekStartDate = calendar.getTime();
				calendar.add(Calendar.DAY_OF_WEEK, 6); // Move to last day of current week
				Date currentWeekEndDate = calendar.getTime();

				String formattedPreviousWeekStartDate = dateFormat.format(previousWeekStartDate);
				String formattedPreviousWeekEndDate = dateFormat.format(previousWeekEndDate);

				String formattedCurrentWeekStartDate = dateFormat.format(currentWeekStartDate);
				String formattedCurrentWeekEndDate = dateFormat.format(previousWeekEndDate);

				fromPriviosDateTime = formattedPreviousWeekStartDate + " " + addedFromTime;
				toPriviosDateTime = formattedPreviousWeekEndDate + " " + addedToTime;

				fromCurrentDateTime = formattedCurrentWeekStartDate + " " + addedFromTime;
				toThisCurrentDateTime = formattedCurrentWeekEndDate + " " + addedToTime;

				fromDate = formattedPreviousWeekStartDate + " " + addedFromTime;
				toDate = formattedCurrentWeekEndDate + " " + addedToTime;

			} else if (value.equals(Constant.LAST_TWO_DAYS)) {

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Calendar calendar = Calendar.getInstance();
				Date todayEndDate = calendar.getTime();
				String formattedTodayEndDate = dateFormat.format(todayEndDate);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				Date todayStartDate = calendar.getTime();
				String formattedTodayStartDate = dateFormat.format(todayStartDate);
				calendar.add(Calendar.DAY_OF_YEAR, -1);
				Date yesterdayEndDate = calendar.getTime();
				String formattedYesterdayEndDate = dateFormat.format(yesterdayEndDate);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				Date yesterdayStartDate = calendar.getTime();
				String formattedYesterdayStartDate = dateFormat.format(yesterdayStartDate);
			}

			List<Event> findEventsBetweenDates = eventRepository.findEventsBetweenDates(fromDate, toDate);
			Map<String, List<Event>> categorizeEventsByDlNo = categorizeEventsByDlNo(findEventsBetweenDates);
			Map<String, Integer> monthWiseTotalCountEvent = getMonthWiseTotalCountEvent(findEventsBetweenDates);
			Map<String, Map<String, Double>> driverEvenntCountMonth = new HashMap<>();

			for (String dlNumber : categorizeEventsByDlNo.keySet()) {

				List<Event> list = categorizeEventsByDlNo.get(dlNumber);
				Map<String, Double> countEventsByMonth = countEventsByMonth(list, monthWiseTotalCountEvent);
				driverEvenntCountMonth.put(list.get(0).getDriverName() + " (" + dlNumber + ")", countEventsByMonth);

			}
			return new Response<>("Success", driverEvenntCountMonth, 200);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response<>("Some Thing Went Wrong", null, 400);
		}
	}

	public static Map<String, Double> countEventsByMonth(List<Event> eventList,
			Map<String, Integer> monthWiseTotalCountEvent) {
		Map<String, Integer> eventCountByMonth = new HashMap<>();
		Calendar calendar = Calendar.getInstance();

		String currentMonthName = getCurrentMonthName();
		String previousMonthName = getPreviousMonthName();

		for (Event event : eventList) {
			calendar.setTime(event.getEventServerCreateTime());
			int month = calendar.get(Calendar.MONTH);
			int year = calendar.get(Calendar.YEAR);
			String monthName = Month.getMonthName(month);
			String key = "";
			if (currentMonthName.equalsIgnoreCase(monthName)) {
				key = "THIS_MONTH";
			}
			if (previousMonthName.equalsIgnoreCase(monthName)) {
				key = "PRIVIOUS_MONTH";
			}
			eventCountByMonth.put(key, eventCountByMonth.getOrDefault(key, 0) + 1);
		}

		Map<String, Double> eventCountByMonthWise = new HashMap<>();

		for (String monthName : eventCountByMonth.keySet()) {

			Integer baseMonthEvent = monthWiseTotalCountEvent.get(monthName);
			Integer actualEvent = eventCountByMonth.get(monthName);

			eventCountByMonthWise.put(monthName, calculatePerformance(actualEvent, baseMonthEvent));

		}

		return eventCountByMonthWise;
	}

	public Map<String, Integer> getMonthWiseTotalCountEvent(List<Event> allEvents) {
		Map<String, Integer> monthCount = new HashMap<>();
		String currentMonthName = getCurrentMonthName();
		String previousMonthName = getPreviousMonthName();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH);

		// Iterate through each event in the list
		for (Event event : allEvents) {
			LocalDateTime eventTime = event.getEventServerCreateTime().toInstant().atZone(ZoneId.systemDefault())
					.toLocalDateTime();

			String month = eventTime.format(formatter); // Format as "MonthName Year"

			if (currentMonthName.equalsIgnoreCase(month)) {
				month = "THIS_MONTH";
			}
			if (previousMonthName.equalsIgnoreCase(month)) {
				month = "PRIVIOUS_MONTH";
			}

			monthCount.put(month, monthCount.getOrDefault(month, 0) + 1);
		}
		return monthCount;
	}

	public static double calculatePerformance(int actualEvents, int baseMonthEvents) {
		return ((double) actualEvents / baseMonthEvents) * 100;
	}

	public static String getCurrentMonthName() {
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		return new DateFormatSymbols().getMonths()[month];
	}

	public static String getPreviousMonthName() {
		Calendar calendar = Calendar.getInstance();
		int previousMonth = (calendar.get(Calendar.MONTH) - 1 + 12) % 12;
		return new DateFormatSymbols().getMonths()[previousMonth];
	}

}
