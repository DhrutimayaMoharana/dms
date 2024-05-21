package com.watsoo.dms.dto;

import com.watsoo.dms.entity.Vehicle;

public class VehicleDto {
    private Long id;
    private String name;
    private String chassisNumber;
    private String color;
    private String engineNumber;
    private String vehicleNumber;

    // Getters and Setters
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

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public static VehicleDto fromEntity(Vehicle vehicle) {
        VehicleDto dto = new VehicleDto();
        dto.setId(vehicle.getId());
        dto.setName(vehicle.getName());
        dto.setChassisNumber(vehicle.getChassisNumber());
        dto.setColor(vehicle.getColor());
        dto.setEngineNumber(vehicle.getEngineNumber());
        dto.setVehicleNumber(vehicle.getVehicleNumber());
        return dto;
    }
}

