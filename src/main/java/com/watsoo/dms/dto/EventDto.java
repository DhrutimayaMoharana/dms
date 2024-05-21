package com.watsoo.dms.dto;

import java.util.Date;
import java.util.List;

import com.watsoo.dms.entity.Event;

public class EventDto {
    private Long id;
    private String vehicleNo;
    private String driverName;
    private String driverPhone;
    private Long driverId;
    private Long vehicleId;
    private Long positionId;
    private String eventType;
    private Date eventTime;
    private List<String> evidencePhotos;
    private List<String> evidenceVideos;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getVehicleNo() {
		return vehicleNo;
	}
	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getDriverPhone() {
		return driverPhone;
	}
	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}
	public Long getDriverId() {
		return driverId;
	}
	public void setDriverId(Long driverId) {
		this.driverId = driverId;
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
	public EventDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	 public static EventDto fromEntity(Event event) {
	        EventDto dto = new EventDto();
	        dto.setId(event.getId());
	        dto.setVehicleNo(event.getVehicleNo());
	        dto.setDriverName(event.getDriverName());
	        dto.setDriverPhone(event.getDriverPhone());
	        dto.setDriverId(event.getDriverId());
	        dto.setVehicleId(event.getVehicleId());
	        dto.setPositionId(event.getPositionId());
	        dto.setEventType(event.getEventType().name());
	        dto.setEventTime(event.getEventTime());
	        return dto;
	    }

    
}

