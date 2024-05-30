package com.watsoo.dms.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.watsoo.dms.restclient.RestClientService;

public class ProcessEvent {
	
	@Autowired
	private RestClientService restClientService;

	@Scheduled(cron = "* * * * * *")
	public void processEvent() {
		
		restClientService.fetchEventDataFromExternalService();
		
		

	}

}
