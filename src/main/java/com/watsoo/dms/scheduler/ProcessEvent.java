package com.watsoo.dms.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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
import com.watsoo.dms.entity.Event;
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

	int schedulerTime = 10;

	Date currentTime = new Date();

	boolean firstCall = true;;

//	@Autowired
//	private ScheduledExecutorService scheduledExecutorService;

	@Scheduled(cron="*/2 * * * * *")
	public void processEvent() {
//		if (lock.tryLock()) {
			try {
				LocalDateTime now = LocalDateTime.now();
				toTime = now.format(DATE_TIME_FORMATTER);

				long between = ChronoUnit.SECONDS.between(LocalDateTime.parse(fromTime, DATE_TIME_FORMATTER), now);

				if (fromTime != null && ChronoUnit.SECONDS.between(LocalDateTime.parse(fromTime, DATE_TIME_FORMATTER),
						now) >= schedulerTime) {

					Optional<Configuration> schedulerTimeCOnfig = configurationRepository
							.findByKey("EVENT_SCHEDULER_TIME");
					if (schedulerTimeCOnfig.isPresent() && !schedulerTimeCOnfig.isEmpty()) {

						schedulerTime = Integer.parseInt(schedulerTimeCOnfig.get().getValue());
					}

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
							Event saveEvent = eventService.saveEvent(events, vehicles);
							if (eventFromTime.isPresent() && saveEvent!=null && saveEvent.getEventServerCreateTime()!=null) {
								Configuration configuration = eventFromTime.get();
								if (saveEvent != null) {
									Date date = saveEvent.getEventServerCreateTime();

									// Convert Date to LocalDateTime
									LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(),
											ZoneId.systemDefault());

									// Add one millisecond
									localDateTime = localDateTime.plusNanos(1_000_000); // Adding one millisecond in
																						// nanoseconds

									// Convert LocalDateTime back to Date
									Date updatedDate = Date
											.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

									// Update configuration with the new date-time
									configuration.setValue(updatedDate.toInstant().toString()); // Assuming
																								// configuration.setValue()
																								// expects a String

									// Save the updated configuration
									configurationRepository.save(configuration);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				
			}
//			finally {
//				lock.unlock();
//			}
//		}
	}

	@Scheduled(cron="*/2 * * * * *")
	public void processCommandSend() {

//		if (lock.tryLock()) {
			try {

				int reCallCount = 0;
				Integer processCommand = 0;
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

				if (firstCall || (new Date(currentTime.getTime() + processCommand * 1000).before(new Date()))) {
					currentTime = new Date();
					firstCall = false;

					commandSendDetalisService.sendCommand(reCallCount, processSleepTime);
					fileUploadDetailsService.updateFlleDetalis(reCallCount);
				}

//				scheduleNextRun(processCommand * 1000L);
//			} finally {
//				lock.unlock();
//			}
		}catch (Exception e) {
			
		}
	}

//	private void scheduleNextRun(long delay) {
//		scheduledExecutorService.schedule(this::processCommandSend, delay, TimeUnit.MILLISECONDS);
//	}
}
