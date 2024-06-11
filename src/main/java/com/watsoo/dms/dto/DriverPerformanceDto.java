package com.watsoo.dms.dto;


public class DriverPerformanceDto {

	private String driverName;
	
	Integer previousRangeCount;
	Integer currentRangeCount;
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public Integer getPreviousRangeCount() {
		return previousRangeCount;
	}
	public void setPreviousRangeCount(Integer previousRangeCount) {
		this.previousRangeCount = previousRangeCount;
	}
	public Integer getCurrentRangeCount() {
		return currentRangeCount;
	}
	public void setCurrentRangeCount(Integer currentRangeCount) {
		this.currentRangeCount = currentRangeCount;
	}
	
	

}
