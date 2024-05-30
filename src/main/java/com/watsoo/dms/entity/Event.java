package com.watsoo.dms.entity;

import java.util.Date;
import com.watsoo.dms.enums.EventType;
import jakarta.persistence.*;

@Entity
@Table(name = "event")
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vehicleNo;
  
    private Long positionId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private Date eventTime;
    
    private String evidencePhotos;
    
    private String evidenceVideos;
    
    private Long deviceId;

    @Column(name="longitude")
    private Double longitude;

    @Column(name="latitude")
    private Double latitude;
   
    public Event() {
    }

    // Getters and Setters for all fields

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
}
