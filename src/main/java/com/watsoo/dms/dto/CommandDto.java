package com.watsoo.dms.dto;

import com.watsoo.dms.entity.Command;

public class CommandDto {
	private Long id;
	private String deviceModelNumber;
	private String baseCommand;
	private String description;
	private Long vechile_id;
	private String imeiNumber;
	private String endCommand;

	// Getters and Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDeviceModelNumber() {
		return deviceModelNumber;
	}

	public void setDeviceModelNumber(String deviceModelNumber) {
		this.deviceModelNumber = deviceModelNumber;
	}

	public String getBaseCommand() {
		return baseCommand;
	}

	public void setBaseCommand(String baseCommand) {
		this.baseCommand = baseCommand;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getVechile_id() {
		return vechile_id;
	}

	public void setVechile_id(Long vechile_id) {
		this.vechile_id = vechile_id;
	}

	public String getImeiNumber() {
		return imeiNumber;
	}

	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}

	public String getEndCommand() {
		return endCommand;
	}

	public void setEndCommand(String endCommand) {
		this.endCommand = endCommand;
	}

	public static CommandDto entityToDto(Command command) {
		CommandDto dto = new CommandDto();
		dto.setId(command.getId());
		dto.setDeviceModelNumber(command.getDdevicModelNumber());
		dto.setBaseCommand(command.getCommand());
		dto.setDescription(command.getDescription());
		return dto;
	}

	public static Command dtoToEntity(CommandDto dto) {
		Command command = new Command();
		command.setId(dto.getId());
		command.setDdevicModelNumber(dto.getDeviceModelNumber());
		command.setCommand(dto.getBaseCommand());
		command.setDescription(dto.getDescription());
		command.setImeiNumber(dto.getImeiNumber());
		command.setVechileId(dto.getVechile_id());
		command.setEndCommand(dto.getEndCommand());
		return command;
	}
}
