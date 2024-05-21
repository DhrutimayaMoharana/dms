package com.watsoo.dms.dto;

public class EventTypeCountDto {

	long yawningCount;
	long mobileUsageCount;
	long distractionCount;
	long smokingCount;
	long closeEyesCount;
	long noFaceCount;
	long lowHeadCount;
	long drinkingCount;
	long totalEventCount;

	public long getYawningCount() {
		return yawningCount;
	}

	public void setYawningCount(long yawningCount) {
		this.yawningCount = yawningCount;
	}

	public long getMobileUsageCount() {
		return mobileUsageCount;
	}

	public void setMobileUsageCount(long mobileUsageCount) {
		this.mobileUsageCount = mobileUsageCount;
	}

	public long getDistractionCount() {
		return distractionCount;
	}

	public void setDistractionCount(long distractionCount) {
		this.distractionCount = distractionCount;
	}

	public long getSmokingCount() {
		return smokingCount;
	}

	public void setSmokingCount(long smokingCount) {
		this.smokingCount = smokingCount;
	}

	public long getCloseEyesCount() {
		return closeEyesCount;
	}

	public void setCloseEyesCount(long closeEyesCount) {
		this.closeEyesCount = closeEyesCount;
	}

	public long getNoFaceCount() {
		return noFaceCount;
	}

	public void setNoFaceCount(long noFaceCount) {
		this.noFaceCount = noFaceCount;
	}

	public long getLowHeadCount() {
		return lowHeadCount;
	}

	public void setLowHeadCount(long lowHeadCount) {
		this.lowHeadCount = lowHeadCount;
	}

	public long getDrinkingCount() {
		return drinkingCount;
	}

	public void setDrinkingCount(long drinkingCount) {
		this.drinkingCount = drinkingCount;
	}

	public long getTotalEventCount() {
		return totalEventCount;
	}

	public void setTotalEventCount(long totalEventCount) {
		this.totalEventCount = totalEventCount;
	}

	public EventTypeCountDto() {
	}

	public EventTypeCountDto(long yawningCount, long mobileUsageCount, long distractionCount,
			long smokingCount, long closeEyesCount, long noFaceCount, long lowHeadCount, long drinkingCount,
			long totalEventCount) {
		this.yawningCount = yawningCount;
		
		this.mobileUsageCount = mobileUsageCount;
		this.distractionCount = distractionCount;
		this.smokingCount = smokingCount;
		this.closeEyesCount = closeEyesCount;
		this.noFaceCount = noFaceCount;
		this.lowHeadCount = lowHeadCount;
		this.drinkingCount = drinkingCount;
		this.totalEventCount = totalEventCount;
	}

}
