package com.watsoo.dms.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.watsoo.dms.entity.Configuration;
import com.watsoo.dms.entity.Vehicle;
import com.watsoo.dms.repository.ConfigurationRepository;
import com.watsoo.dms.repository.VehicleRepository;
import com.watsoo.dms.restclient.RestClientService;
import com.watsoo.dms.service.EventService;

@Component
@EnableScheduling
public class ProcessEvent {

	@Autowired
	private RestClientService restClientService;

	@Autowired
	private EventService eventService;

	@Autowired
	private VehicleRepository vehicleRepository;

	@Autowired
	private ConfigurationRepository configurationRepository;

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
			.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	String fromTime = "2024-05-29T18:30:00.000Z";
	String toTime = null;

	@Value("${scheduler.interval.second}")
	int schedulerTime;

	@Scheduled(fixedRate = 1000)
	public void processEvent() {
		LocalDateTime now = LocalDateTime.now();
		String toTime = now.format(DATE_TIME_FORMATTER);
		if (fromTime != null && ChronoUnit.SECONDS.between(LocalDateTime.parse(fromTime, DATE_TIME_FORMATTER),
				now) >= schedulerTime) {

			List<Vehicle> vehicles = vehicleRepository.findAll();
			List<Integer> vehiclesDeviceIds = vehicles.stream().map(Vehicle::getDeviceId).collect(Collectors.toList());
			if (vehiclesDeviceIds != null && vehiclesDeviceIds.size() > 0) {
				Optional<Configuration> eventFromTime = configurationRepository.findByKey("EVENT_FROM_TIME");
				if (eventFromTime.isPresent() && eventFromTime.get() != null
						&& eventFromTime.get().getValue() != null) {
					fromTime = eventFromTime.get().getValue();
				}
				String events = restClientService.fetchEventDataFromExternalService(vehiclesDeviceIds, "alarm",
						fromTime, toTime);
				if (events != null) {
					eventService.saveEvent(events, vehicles);
					if (eventFromTime.isPresent()) {
						Configuration configuration = eventFromTime.get();
						configuration.setValue(toTime);
//						configurationRepository.save(configuration);
					}

				}
			}
		}
	}

}
