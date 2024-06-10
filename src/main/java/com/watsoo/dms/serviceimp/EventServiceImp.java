package com.watsoo.dms.serviceimp;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.watsoo.dms.dto.DriverPerformanceDto;
import com.watsoo.dms.dto.EventDto;
import com.watsoo.dms.dto.EventTypeCountDto;
import com.watsoo.dms.dto.PaginatedRequestDto;
import com.watsoo.dms.dto.PaginatedResponseDto;
import com.watsoo.dms.dto.Response;
import com.watsoo.dms.entity.Configuration;
import com.watsoo.dms.entity.CredentialMaster;
import com.watsoo.dms.entity.Event;
import com.watsoo.dms.entity.User;
import com.watsoo.dms.entity.Vehicle;
import com.watsoo.dms.enums.EventType;
import com.watsoo.dms.repository.ConfigurationRepository;
import com.watsoo.dms.repository.DriverRepository;
import com.watsoo.dms.repository.EventRepository;
import com.watsoo.dms.repository.RemarkRepository;
import com.watsoo.dms.repository.VehicleRepository;
import com.watsoo.dms.restclient.RestClientService;
import com.watsoo.dms.security.JwtUserDetailsService;
import com.watsoo.dms.service.CommandSendDetalisService;
import com.watsoo.dms.service.EventService;
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

	@Autowired
	private JwtUserDetailsService userDetailsService;

	@Value("${file.get.url}")
	String fileUrl;

	Map<Long, String> deviceInformationWithUser = new HashMap<>();
	Map<Long, Date> userWithTime = new HashMap<>();
	String timeConfiguration = "10";

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
			String deviceInformation = getDeviceInformtaion(allDeviceID);
			Integer pendingRemark = 0;
			Integer actionTakenRemark = 0;
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

					if (event.getRemark().equals("PENDING")) {
						pendingRemark++;
					} else {
						actionTakenRemark++;
					}
					eventsdto.add(fromEventToEventDto);
				}
			}

			eventsAllDetalisForDashBoard.setPendingRemark(pendingRemark);
			eventsAllDetalisForDashBoard.setActionTakenRemark(actionTakenRemark);
			PaginatedResponseDto<Object> paginatedResponseDto = new PaginatedResponseDto<>(eventRepository.count(),
					events.size(), findAllEvent.getTotalPages(), pageNo, eventsdto, eventsAllDetalisForDashBoard);
			return new Response<>("Event List", paginatedResponseDto, HttpStatus.OK.value());

		} catch (Exception e) {
			return new Response<>("Error occurred while processing Event list", null, HttpStatus.BAD_REQUEST.value());

		}

	}

	public String getDeviceInformtaion(Set<Long> allDeviceID) {

		boolean isFirstCall = true;

		Date currentTime = new Date();

		User user = new User();
		Optional<CredentialMaster> userDetails = userDetailsService.getUserDetails();
		if (userDetails.isPresent()) {
			user = userDetails.get().getUser();
		}
		if (deviceInformationWithUser != null && userWithTime != null
				&& deviceInformationWithUser.get(user.getId()) != null) {
			isFirstCall = false;
			currentTime = userWithTime.get(user.getId());
		}

		if (isFirstCall
				|| (!isFirstCall && (new Date(currentTime.getTime() + Integer.parseInt(timeConfiguration) * 60000)
						.before(new Date())))) {
			Optional<Configuration> findByKey = configurationRepository.findByKey("DEVICE_INFORMATION_API_CALL_TIME");
			if (findByKey.isPresent() && findByKey.get() != null) {
				timeConfiguration = findByKey.get().getValue();
			}

			String deviceInformation = restClientService.getDeviceInformation(allDeviceID);
			deviceInformationWithUser.put(user.getId(), deviceInformation);
			userWithTime.put(user.getId(), new Date());
			return deviceInformation;

		} else {
			return deviceInformationWithUser.get(user.getId());
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

		String fromPriviosStartDateTime = "";
		String toPriviosEndDateTime = "";

		String fromCurrentStartDateTime = "";
		String toCurrentEndDateTime = "";

		if (value == null) {
			return new Response<>("Value Must Be required", null, 400);
		}

		try {

			if (value.equals(Constant.LAST_TWO_MONTHS)) {
				Calendar calendar = Calendar.getInstance();

				// Previous Month Start Date
				calendar.add(Calendar.MONTH, -1);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				Date previousMonthStartDate = calendar.getTime();

				// Previous Month End Date
				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				Date previousMonthEndDate = calendar.getTime();

				// Current Month Start Date
				calendar = Calendar.getInstance();
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				Date currentMonthStartDate = calendar.getTime();

				// Current Month End Date
				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				Date currentMonthEndDate = calendar.getTime();

				// Formatting dates
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String formattedPreviousMonthStartDate = dateFormat.format(previousMonthStartDate);
				String formattedPreviousMonthEndDate = dateFormat.format(previousMonthEndDate);
				String formattedCurrentMonthStartDate = dateFormat.format(currentMonthStartDate);
				String formattedCurrentMonthEndDate = dateFormat.format(currentMonthEndDate);

				fromPriviosStartDateTime = formattedPreviousMonthStartDate + " " + addedFromTime;
				toPriviosEndDateTime = formattedPreviousMonthEndDate + " " + addedToTime;

				fromCurrentStartDateTime = formattedCurrentMonthStartDate + " " + addedFromTime;
				toCurrentEndDateTime = formattedCurrentMonthEndDate + " " + addedToTime;

				fromDate = formattedPreviousMonthStartDate + " " + addedFromTime;
				toDate = formattedCurrentMonthEndDate + " " + addedToTime;

			} else if (value.equals(Constant.LAST_TWO_WEEK)) {

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
				String formattedCurrentWeekEndDate = dateFormat.format(currentWeekEndDate);

				fromPriviosStartDateTime = formattedPreviousWeekStartDate + " " + addedFromTime;
				toPriviosEndDateTime = formattedPreviousWeekEndDate + " " + addedToTime;

				fromCurrentStartDateTime = formattedCurrentWeekStartDate + " " + addedFromTime;
				toCurrentEndDateTime = formattedCurrentWeekEndDate + " " + addedToTime;

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

				fromPriviosStartDateTime = formattedYesterdayStartDate + " " + addedFromTime;
				toPriviosEndDateTime = formattedYesterdayEndDate + " " + addedToTime;
				fromCurrentStartDateTime = formattedTodayStartDate + " " + addedFromTime;
				toCurrentEndDateTime = formattedTodayEndDate + " " + addedToTime;
				fromDate = formattedYesterdayStartDate + " " + addedFromTime;
				toDate = formattedTodayEndDate + " " + addedToTime;

			} else {

				return new Response<>("Provide The Valid Data in  Value Key ", null, 400);
			}

			List<Event> findEventsBetweenDates = eventRepository.findEventsBetweenDates(fromDate, toDate);

			Map<String, Integer> twoDateBetweenEvent = getDataBetweenTwoDates(findEventsBetweenDates,
					fromPriviosStartDateTime, toPriviosEndDateTime, fromCurrentStartDateTime, toCurrentEndDateTime);

			Map<String, List<Event>> categorizeEventsByDlNo = categorizeEventsByDlNo(findEventsBetweenDates);
			Map<String, Map<String, Double>> driverEvenntCountMonth = new HashMap<>();

			List<DriverPerformanceDto> driverPerformanceDto = new ArrayList<>();

			for (String dlNumber : categorizeEventsByDlNo.keySet()) {

				DriverPerformanceDto obj = new DriverPerformanceDto();

				List<Event> list = categorizeEventsByDlNo.get(dlNumber);
				Map<String, Double> countEventsByMonth = countEventsByRange(list, twoDateBetweenEvent,
						fromPriviosStartDateTime, toPriviosEndDateTime, fromCurrentStartDateTime, toCurrentEndDateTime);

				obj.setDriverName(list.get(0).getDriverName() + " (" + dlNumber + ")");
				for (Map.Entry<String, Double> rangeCountByDriver : countEventsByMonth.entrySet()) {
					String key = rangeCountByDriver.getKey();

					if (key.equals("previousRangeCount")) {
						obj.setPreviousRangeCount(rangeCountByDriver.getValue());
					}
					if (key.equals("currentRangeCount")) {
						obj.setCurrentRangeCount(rangeCountByDriver.getValue());
					}

				}

				driverPerformanceDto.add(obj);
				
//				driverEvenntCountMonth.put(list.get(0).getDriverName() + " (" + dlNumber + ")", countEventsByMonth);

			}

			return new Response<>("Success", driverPerformanceDto, 200);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response<>("Some Thing Went Wrong", null, 400);
		}
	}

	private Map<String, Double> countEventsByRange(List<Event> eventList, Map<String, Integer> twoDateBetweenEvent,
			String fromPriviosStartDateTime, String toPriviosEndDateTime, String fromCurrentStartDateTime,
			String toCurrentEndDateTime) {

		Map<String, Integer> eventCountByMonth = new HashMap<>();

		for (Event event : eventList) {

			String key = getRangeCount(event.getEventServerCreateTime(), fromPriviosStartDateTime, toPriviosEndDateTime,
					fromCurrentStartDateTime, toCurrentEndDateTime);

			eventCountByMonth.put(key, eventCountByMonth.getOrDefault(key, 0) + 1);
		}

		Map<String, Double> eventCountByMonthWise = new HashMap<>();

		for (String key : eventCountByMonth.keySet()) {

			Integer baseMonthEvent = twoDateBetweenEvent.get(key);
			Integer actualEvent = eventCountByMonth.get(key);

			eventCountByMonthWise.put(key, calculatePerformance(actualEvent, baseMonthEvent));

		}

		return eventCountByMonthWise;

	}

	public Map<String, Integer> getDataBetweenTwoDates(List<Event> findEventsBetweenDates,
			String fromPreviousStartDateTime, String toPreviousEndDateTime, String fromCurrentStartDateTime,
			String toCurrentEndDateTime) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			Date fromPreviousStartDate = dateFormat.parse(fromPreviousStartDateTime);
			Date toPreviousEndDate = dateFormat.parse(toPreviousEndDateTime);
			Date fromCurrentStartDate = dateFormat.parse(fromCurrentStartDateTime);
			Date toCurrentEndDate = dateFormat.parse(toCurrentEndDateTime);
			long previousRangeCount = findEventsBetweenDates.stream().filter(
					event -> isWithinRange(event.getEventServerCreateTime(), fromPreviousStartDate, toPreviousEndDate))
					.count();

			long currentRangeCount = findEventsBetweenDates.stream().filter(
					event -> isWithinRange(event.getEventServerCreateTime(), fromCurrentStartDate, toCurrentEndDate))
					.count();
			Map<String, Integer> result = new HashMap<>();
			result.put("previousRangeCount", (int) previousRangeCount);
			result.put("currentRangeCount", (int) currentRangeCount);

			return result;

		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private boolean isWithinRange(Date eventDate, Date startDate, Date endDate) {
		return eventDate != null && !eventDate.before(startDate) && !eventDate.after(endDate);
	}

	public String getRangeCount(Date serverCreateTime, String fromPreviousStartDateTime, String toPreviousEndDateTime,
			String fromCurrentStartDateTime, String toCurrentEndDateTime) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			Date fromPreviousStartDate = dateFormat.parse(fromPreviousStartDateTime);
			Date toPreviousEndDate = dateFormat.parse(toPreviousEndDateTime);
			Date fromCurrentStartDate = dateFormat.parse(fromCurrentStartDateTime);
			Date toCurrentEndDate = dateFormat.parse(toCurrentEndDateTime);
			if (isWithinRange(serverCreateTime, fromPreviousStartDate, toPreviousEndDate)) {
				return "previousRangeCount";
			}

			if (isWithinRange(serverCreateTime, fromCurrentStartDate, toCurrentEndDate)) {
				return "currentRangeCount";
			}

			return "outOfRange";

		} catch (ParseException e) {
			e.printStackTrace();
			return "error";
		}
	}

	public static Double calculatePerformance(int actualEvents, int baseMonthEvents) {
		if (baseMonthEvents == 0) {
			throw new IllegalArgumentException("baseMonthEvents cannot be zero");
		}

		double performance = ((double) actualEvents / baseMonthEvents) * 100;
		DecimalFormat df = new DecimalFormat("#0.00");

		return Double.valueOf(df.format(performance));
	}

}
