package com.watsoo.dms.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.watsoo.dms.entity.Configuration;
import com.watsoo.dms.entity.Vehicle;
import com.watsoo.dms.repository.CommandSendDetalisRepository;
import com.watsoo.dms.repository.ConfigurationRepository;
import com.watsoo.dms.repository.VehicleRepository;
import com.watsoo.dms.restclient.RestClientService;
import com.watsoo.dms.service.CommandSendDetalisService;
import com.watsoo.dms.service.EventService;
import com.watsoo.dms.service.FileUploadDetailsService;

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

	@Autowired
	private CommandSendDetalisRepository commandSendDetalisRepository;

//	@Autowired
//	private ScheduledExecutorService scheduledExecutorService;

	@Autowired
	private CommandSendDetalisService commandSendDetalisService;
	
	@Autowired
	private FileUploadDetailsService fileUploadDetailsService;

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
			.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	private static final ReentrantLock lock = new ReentrantLock();

	String fromTime = "2024-05-29T18:30:00.000Z";
	String toTime = null;

	@Value("${scheduler.interval.second}")
	int schedulerTime;

//	@Scheduled(fixedRate = 1000)
	public void processEvent() {
		if (lock.tryLock()) {
			try {
				LocalDateTime now = LocalDateTime.now();
				toTime = now.format(DATE_TIME_FORMATTER);
				if (fromTime != null && ChronoUnit.SECONDS.between(LocalDateTime.parse(fromTime, DATE_TIME_FORMATTER),
						now) >= schedulerTime) {
					List<Vehicle> vehicles = vehicleRepository.findAll();
					List<Integer> vehiclesDeviceIds = vehicles.stream().map(Vehicle::getDeviceId)
							.collect(Collectors.toList());
					if (vehiclesDeviceIds != null && vehiclesDeviceIds.size() > 0) {
						Optional<Configuration> eventFromTime = configurationRepository.findByKey("EVENT_FROM_TIME");
						if (eventFromTime.isPresent() && eventFromTime.get().getValue() != null) {
							fromTime = eventFromTime.get().getValue();
						}
						String events = restClientService.fetchEventDataFromExternalService(vehiclesDeviceIds, "alarm",
								fromTime, toTime);
						if (events != null) {
							eventService.saveEvent(events, vehicles);
							if (eventFromTime.isPresent()) {
								Configuration configuration = eventFromTime.get();
								configuration.setValue(toTime);
								 configurationRepository.save(configuration);
							}
						}
					}
				}
			} finally {
				lock.unlock();
			}
		}
	}

	@Scheduled(fixedRate = 1000)
	public void processCommandSend() {

		if (lock.tryLock()) {
			try {
				int reCallCount = 0;
				int processCommand = 0;
				int processSleepTime = 0;
				List<Configuration> allConfiguration = configurationRepository.findAll();
				for (Configuration configuration : allConfiguration) {
					if (configuration.getKey().equals("RE_CAll_COUNT")) {
						reCallCount = Integer.valueOf(configuration.getValue());
					}
					if (configuration.getKey().equals("PROCESS_COMMAND_TIME")) {
						processCommand = Integer.valueOf(configuration.getValue());
					}
					if (configuration.getKey().equals("PROCESS_COMMAND_SLEEP_TIME")) {
						processSleepTime = Integer.valueOf(configuration.getValue());
					}
				}
//				commandSendDetalisService.sendCommand(reCallCount,processSleepTime);
				fileUploadDetailsService.updateFlleDetalis();
				
//				scheduleNextRun(processCommand * 1000L);
			} finally {
				lock.unlock();
			}
		}
	}

//	private void scheduleNextRun(long delay) {
//		scheduledExecutorService.schedule(this::processCommandSend, delay, TimeUnit.MILLISECONDS);
//	}
}
