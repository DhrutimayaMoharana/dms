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
	
    private Double longitude;
    private Double latitude;

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

	public static EventDto fromEntity(Event event) {
		EventDto dto = new EventDto();
		dto.setId(event.getId());
//		dto.setVehicleId(event.getVehicleId());
//		dto.setVehicleId(event.getVehicle().getId());
//		dto.setVehicleDto(VehicleDto.fromEntity(event.getVehicle()));
		dto.setPositionId(event.getPositionId());
		dto.setEventType(event.getEventType().name());
		dto.setEventTime(event.getEventTime());
		return dto;
	}

}
