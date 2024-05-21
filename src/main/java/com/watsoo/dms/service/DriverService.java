package com.watsoo.dms.service;

import com.watsoo.dms.dto.DriverDto;
import com.watsoo.dms.dto.Response;

public interface DriverService {

	Response<?> saveDriver(DriverDto driverDto);

	Response<?> getDriverById(Long driverId);

}
