package com.watsoo.dms.serviceimp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.watsoo.dms.dto.DriverDto;
import com.watsoo.dms.dto.EventDto;
import com.watsoo.dms.dto.EventTypeCountDto;
import com.watsoo.dms.dto.PaginatedRequestDto;
import com.watsoo.dms.dto.PaginatedResponseDto;
import com.watsoo.dms.dto.Response;
import com.watsoo.dms.dto.VehicleDto;
import com.watsoo.dms.entity.Driver;
import com.watsoo.dms.entity.Event;
import com.watsoo.dms.entity.Vehicle;
import com.watsoo.dms.enums.EventType;
import com.watsoo.dms.repository.DriverRepository;
import com.watsoo.dms.repository.EventRepository;
import com.watsoo.dms.repository.VehicleRepository;
import com.watsoo.dms.restclient.RestClientService;
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

	@Override
	public Response<?> getAllEvent(int pageSize, int pageNo, String vehicleNo, String driverName, String eventType,
			String searchKey) {

		try {
			PaginatedRequestDto paginatedRequest = new PaginatedRequestDto(pageSize, pageNo, vehicleNo, driverName,
					eventType, searchKey);
			Pageable pageable = pageSize > 0 ? PageRequest.of(pageNo, pageSize) : Pageable.unpaged();
			Page<Event> findAllEvent = eventRepository.findAll(paginatedRequest, pageable);
			List<Event> events = findAllEvent.getContent();

			List<Long> allDeviceID = events.stream().map(event -> {
				return event.getDeviceId();
			}).collect(Collectors.toList());

			Map<Integer, Vehicle> vehiclesByDeviceId = new HashMap<>();
			Map<Long, Driver> allDriver = new HashMap<>();
			List<Long> vechileIdList = new ArrayList<>();

			if (allDeviceID != null && !allDeviceID.isEmpty()) {
				List<Vehicle> vehicles = vehicleRepository.findVehiclesByDeviceIds(allDeviceID);
				vehiclesByDeviceId = vehicles.stream()
						.collect(Collectors.toMap(Vehicle::getDeviceId, Function.identity()));

				vechileIdList = vehicles.stream().map(Vehicle::getId).collect(Collectors.toList());

				List<Driver> allDrivers = driverRepository.findAllById(vechileIdList);
				allDriver = allDrivers.stream().collect(Collectors.toMap(Driver::getVehicle_id, Function.identity()));
			}

			List<EventDto> eventsdto = new ArrayList<>();

			// Iterate over the events list
			for (Event event : events) {
				EventDto fromEventToEventDto = EventDto.fromEntity(event);
				if (event.getEvidencePhotos() != null) {
					fromEventToEventDto.setEvidencePhotos(convertStringToArray(event.getEvidencePhotos()));
				}
				Long deviceId = event.getDeviceId();
				Vehicle vehicle = vehiclesByDeviceId.get(deviceId.intValue()); // Convert Long to Integer
				fromEventToEventDto.setVehicleDto(VehicleDto.fromEntity(vehicle));
				Driver driver = allDriver.get(vehicle.getId());
				fromEventToEventDto.setDriverDto(DriverDto.convertEntityToDto(driver));

				eventsdto.add(fromEventToEventDto);
			}

			PaginatedResponseDto<Object> paginatedResponseDto = new PaginatedResponseDto<>(eventRepository.count(),
					events.size(), findAllEvent.getTotalPages(), pageNo, eventsdto);
			return new Response<>("Event List", paginatedResponseDto, HttpStatus.OK.value());

		} catch (Exception e) {
			e.printStackTrace();
			return new Response<>("Error occurred while processing Event list", null, HttpStatus.BAD_REQUEST.value());

		}

	}

	private List<String> convertStringToArray(String evidencePhotos) {
		String[] urlArray = evidencePhotos.split(",");
		return Arrays.asList(urlArray);

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
	public void saveEvent(String events) {
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
						positionIdList.add(positionId);
						eventsDataMap.put(positionId, eventObject);
					}

				}

			}

		}

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

							}

						}
					}

				}

			}

		}

		if (eventsDataMap != null && !eventsDataMap.isEmpty()) {
			List<Event> saveAll = eventRepository.saveAll(eventsDataMap.values());
		}

	}

}
