package com.watsoo.dms.service;

import com.watsoo.dms.dto.Response;

public interface EventService {

	Response<?> getAllEvent(int pageSize, int pageNo, String vehicleNo, String driverName, String eventType,
			String SearchKey);

	Response<?> fetchDashBoardCounts(String value);

	Response<?> getAllEventType();

	void saveEvent(String events);

}
