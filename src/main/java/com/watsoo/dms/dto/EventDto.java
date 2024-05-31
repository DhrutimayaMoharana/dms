package com.watsoo.dms.dto;

import java.util.Date;
import java.util.List;

import com.watsoo.dms.entity.Event;

import jakarta.persistence.Column;

public class EventDto {
	private Long id;
	private Long vehicleId;
	private VehicleDto vehicleDto;
	private Long positionId;
	private String eventType;
	private Date eventTime;
	private List<String> evidencePhotos;
	private List<String> evidenceVideos; 
	private Date eventServerCreateTime;
	private Long deviceId;
	private Double longitude;
	private Double latitude;
	private DriverDto driverDto;
	
   

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public Long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(Long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public Long getPositionId() {
		return positionId;
	}

	public void setPositionId(Long positionId) {
		this.positionId = positionId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Date getEventTime() {
		return eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public List<String> getEvidencePhotos() {
		return evidencePhotos;
	}

	public void setEvidencePhotos(List<String> evidencePhotos) {
		this.evidencePhotos = evidencePhotos;
	}

	public List<String> getEvidenceVideos() {
		return evidenceVideos;
	}

	public void setEvidenceVideos(List<String> evidenceVideos) {
		this.evidenceVideos = evidenceVideos;
	}

	public VehicleDto getVehicleDto() {
		return vehicleDto;
	}

	public void setVehicleDto(VehicleDto vehicleDto) {
		this.vehicleDto = vehicleDto;
	}

	public EventDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Date getEventServerCreateTime() {
		return eventServerCreateTime;
	}

	public void setEventServerCreateTime(Date eventServerCreateTime) {
		this.eventServerCreateTime = eventServerCreateTime;
	}

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public DriverDto getDriverDto() {
		return driverDto;
	}

	public void setDriverDto(DriverDto driverDto) {
		this.driverDto = driverDto;
	}

	public static EventDto fromEntity(Event event) {
		EventDto dto = new EventDto();
		dto.setId(event.getId());
		dto.setPositionId(event.getPositionId());
		dto.setEventType(event.getEventType().name());
		dto.setEventTime(event.getEventTime());
		dto.setDeviceId(event.getDeviceId());
		dto.setLatitude(event.getLatitude());
		dto.setLongitude(event.getLongitude());
		return dto;
	}

}
