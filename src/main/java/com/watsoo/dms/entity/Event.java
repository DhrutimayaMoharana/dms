package com.watsoo.dms.entity;

import java.util.Date;

import com.watsoo.dms.enums.EventType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "event")
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String vehicleNo;
	private String driverName;
	private String driverPhone;
	private Long driverId;
	private Long vehicleId;
	private Long positionId;

	@Enumerated(EnumType.STRING)
	private EventType eventType;
	private Date eventTime;
	private String evidencePhotos;
	private String evidenceVideos;

	public Event() {
	}

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

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public Date getEventTime() {
		return eventTime;
	}

	public void setEventTime(Date eventTime) {
		this.eventTime = eventTime;
	}

	public String getEvidencePhotos() {
		return evidencePhotos;
	}

	public void setEvidencePhotos(String evidencePhotos) {
		this.evidencePhotos = evidencePhotos;
	}

	public String getEvidenceVideos() {
		return evidenceVideos;
	}

	public void setEvidenceVideos(String evidenceVideos) {
		this.evidenceVideos = evidenceVideos;
	}
}
