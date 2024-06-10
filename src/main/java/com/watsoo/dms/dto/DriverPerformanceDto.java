package com.watsoo.dms.dto;


public class DriverPerformanceDto {

	private String driverName;
	
	Double previousRangeCount;
	Double currentRangeCount;
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public Double getPreviousRangeCount() {
		return previousRangeCount;
	}
	public void setPreviousRangeCount(Double previousRangeCount) {
		this.previousRangeCount = previousRangeCount;
	}
	public Double getCurrentRangeCount() {
		return currentRangeCount;
	}
	public void setCurrentRangeCount(Double currentRangeCount) {
		this.currentRangeCount = currentRangeCount;
	}
	

	

}
