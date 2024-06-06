package com.watsoo.dms.service;

import java.util.List;

import com.watsoo.dms.dto.EventDto;
import com.watsoo.dms.dto.Response;
import com.watsoo.dms.entity.Vehicle;

public interface EventService {

	Response<?> getAllEvent(int pageSize, int pageNo, String vehicleNo, String driverName, String eventType,
			String SearchKey,String fromDate,String toDate,String dlNumber);

	Response<?> fetchDashBoardCounts(String value);

	Response<?> getAllEventType();

	void saveEvent(String events, List<Vehicle> vehicles);

	Response<?> getEventById(Long eventId);

	Response<?> updateEvent(EventDto eventDto);

	Response<?> getEventDetalisForDashBoard();

	Response<?> getEventDetalisForDriverPerfomance(String value);

}
