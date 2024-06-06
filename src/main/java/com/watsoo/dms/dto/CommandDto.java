package com.watsoo.dms.dto;

import com.watsoo.dms.entity.Command;

public class CommandDto {
	private Long id;
	private String deviceModelNumber;
	private String command;
	private String description;
	private CommandTypeDTO commandTypeDTO;

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

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public CommandTypeDTO getCommandTypeDTO() {
		return commandTypeDTO;
	}

	public void setCommandTypeDTO(CommandTypeDTO commandTypeDTO) {
		this.commandTypeDTO = commandTypeDTO;
	}

	
	public static CommandDto entityToDto(Command command) {
		CommandDto dto = new CommandDto();
		dto.setId(command.getId());
		dto.setDeviceModelNumber(command.getDdevicModelNumber());
		dto.setCommand(command.getCommand());
		dto.setDescription(command.getDescription());
		dto.setCommandTypeDTO(CommandTypeDTO.convertToDTO(command.getCommandType()));
		return dto;
	}

	
	public static Command dtoToEntity(CommandDto dto) {
		Command command = new Command();
		command.setId(dto.getId());
		command.setDdevicModelNumber(dto.getDeviceModelNumber());
		command.setCommand(dto.getCommand());
		command.setDescription(dto.getDescription());
		return command;
	}
}
