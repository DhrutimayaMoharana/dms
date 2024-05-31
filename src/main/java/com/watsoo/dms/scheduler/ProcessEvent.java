package com.watsoo.dms.scheduler;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.watsoo.dms.restclient.RestClientService;
import com.watsoo.dms.service.EventService;

@Component
@EnableScheduling
public class ProcessEvent {

	@Autowired
	private RestClientService restClientService;
	
	@Autowired
	private EventService eventService;

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
			.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

//	@Scheduled(cron = "0/2 * * * * *")
//	public void processEvent() {
//		String fromtime = "2024-05-29T18:30:00.000Z";
//		String toTime = "2024-05-30T18:30:00.000Z";
//		String events = restClientService.fetchEventDataFromExternalService(2, "alarm", fromtime, toTime);
//		if(events!=null) {
//			eventService.saveEvent(events);
//		}}
		

	

}
