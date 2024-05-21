package com.watsoo.dms.dto;

import com.watsoo.dms.entity.Driver;

public class DriverDto {

	private Long id;

	private String name;

	private String phoneNumber;

	private int age;

	private String dob;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public DriverDto() {
		super();
	}

	public static Driver convertDtoToEntity(DriverDto dto) {
		Driver driver = new Driver();
		driver.setName(dto.getName());
		driver.setPhoneNumber(dto.getPhoneNumber());
		driver.setAge(dto.getAge());
		driver.setDob(dto.getDob());
		return driver;
	}

	public static DriverDto convertEntityToDto(Driver driver) {
		DriverDto driverDto = new DriverDto();
		driverDto.setId(driver.getId());
		driverDto.setName(driver.getName());
		driverDto.setPhoneNumber(driver.getPhoneNumber());
		driverDto.setAge(driver.getAge());
		driverDto.setDob(driver.getDob());
		return driverDto;
	}

}
