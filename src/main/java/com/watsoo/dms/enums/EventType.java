package com.watsoo.dms.enums;

public enum EventType {

	CLOSE_EYES("closeEyes"), DISTRACTION("distraction"), LOW_HEAD("lowHead"), DRINKING("drinking"), NO_FACE("noFace"),
	PHONE_CALLING("phoneCalling"), SMOKING_ALERT("smoking"), YAWN_ALERT("yawning");

	private final String type;

	EventType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static EventType fromType(String type) {
		for (EventType eventType : values()) {
			if (eventType.getType().equalsIgnoreCase(type)) {
				return eventType;
			}
		}
		return null;
	}
}
