package com.watsoo.dms.serviceimp;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.watsoo.dms.dto.DriverDto;
import com.watsoo.dms.dto.Response;
import com.watsoo.dms.entity.Driver;
import com.watsoo.dms.repository.DriverRepository;
import com.watsoo.dms.service.DriverService;

@Service
public class DriverServiceImpl implements DriverService {

	@Autowired
	private DriverRepository driverRepository;

	@Override
	public Response<?> saveDriver(DriverDto driverDto) {

		Driver convertDtoToEntity = DriverDto.convertDtoToEntity(driverDto);

		Driver save = driverRepository.save(convertDtoToEntity);

		return new Response<>("Driver Add Succesfully", DriverDto.convertEntityToDto(save), HttpStatus.OK.value());
	}

	@Override
	public Response<?> getDriverById(Long driverId) {

		Optional<Driver> findDriverById = driverRepository.findById(driverId);
		if (findDriverById.isPresent()) {
			return new Response<>("Success", DriverDto.convertEntityToDto(findDriverById.get()), HttpStatus.OK.value());
		}
		return new Response<>("No data found", null, HttpStatus.BAD_REQUEST.value());
	}

}
