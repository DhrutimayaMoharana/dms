package com.watsoo.dms.dto;

public class PaginatedRequestDto {

	private int pageSize;

	private int pageNo;

	private String vehicleNo;
	
	private String vehicleName;

	private String driverName;

	private String eventType;
	
	private String searchKey;

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
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

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}
	

	public String getVehicleName() {
		return vehicleName;
	}

	public void setVehicleName(String vehicleName) {
		this.vehicleName = vehicleName;
	}

	public PaginatedRequestDto(int pageSize, int pageNo, String vehicleNo, String driverName, String eventType,String searchKey) {
		this.pageSize = pageSize;
		this.pageNo = pageNo;
		this.driverName = driverName;
		this.eventType = eventType;
		this.vehicleNo = vehicleNo;
		this.searchKey=searchKey;
	}
	public PaginatedRequestDto(int pageSize, int pageNo, String vehicleNo, String vehicleName) {
		this.pageSize = pageSize;
		this.pageNo = pageNo;
		this.vehicleName = vehicleName;
		this.vehicleNo = vehicleNo;
		
	}

}
