package com.watsoo.dms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PaginatedRequestDto {

	private int pageSize;

	private int pageNo;

	private String vehicleNo;

	private String vehicleName;

	private String driverName;

	private String eventType;

	private String searchKey;

	private String dlNumber;

	private String deviceModel;
	
	private String imeiNumber;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private String fromDate;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private String toDate;

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

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getDlNumber() {
		return dlNumber;
	}

	public void setDlNumber(String dlNumber) {
		this.dlNumber = dlNumber;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getImeiNumber() {
		return imeiNumber;
	}

	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}

	public PaginatedRequestDto(int pageSize, int pageNo, String vehicleNo, String driverName, String eventType,
			String searchKey) {
		this.pageSize = pageSize;
		this.pageNo = pageNo;
		this.driverName = driverName;
		this.eventType = eventType;
		this.vehicleNo = vehicleNo;
		this.searchKey = searchKey;
	}

	public PaginatedRequestDto(int pageSize, int pageNo, String vehicleNo, String driverName, String eventType,
			String searchKey, String fromDate, String toDate) {
		this.pageSize = pageSize;
		this.pageNo = pageNo;
		this.driverName = driverName;
		this.eventType = eventType;
		this.vehicleNo = vehicleNo;
		this.searchKey = searchKey;
		this.toDate = toDate;
		this.fromDate = fromDate;
	}

	public PaginatedRequestDto(int pageSize, int pageNo, String vehicleNo, String driverName, String eventType,
			String searchKey, String fromDate, String toDate, String dlNumber) {
		this.pageSize = pageSize;
		this.pageNo = pageNo;
		this.driverName = driverName;
		this.eventType = eventType;
		this.vehicleNo = vehicleNo;
		this.searchKey = searchKey;
		this.toDate = toDate;
		this.fromDate = fromDate;
		this.dlNumber = dlNumber;
	}

	public PaginatedRequestDto(int pageSize, int pageNo, String vehicleNo, String vehicleName,String imeiNumber) {
		this.pageSize = pageSize;
		this.pageNo = pageNo;
		this.vehicleName = vehicleName;
		this.vehicleNo = vehicleNo;
		this.imeiNumber=imeiNumber;

	}

	public PaginatedRequestDto(int pageSize, int pageNo) {
		this.pageSize = pageSize;
		this.pageNo = pageNo;

	}

	public PaginatedRequestDto(int pageSize, int pageNo, String deviceModel) {
		this.pageSize = pageSize;
		this.pageNo = pageNo;
		this.deviceModel = deviceModel;

	}

}
