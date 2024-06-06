package com.watsoo.dms.dto;

import java.util.Date;

import com.watsoo.dms.entity.CommandSendTrail;

public class CommandSendTrailDto {

	private Long id;

	private UserDto dto;

	private String command;

	private Long vechileId;

	private Date createdOn;

	private Date updatedOn;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserDto getDto() {
		return dto;
	}

	public void setDto(UserDto dto) {
		this.dto = dto;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public Long getVechileId() {
		return vechileId;
	}

	public void setVechileId(Long vechileId) {
		this.vechileId = vechileId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public CommandSendTrailDto() {

	}

	public static CommandSendTrailDto entityToDto(CommandSendTrail commandSendTrail) {
		
		CommandSendTrailDto obj = new CommandSendTrailDto();

		obj.setCommand(commandSendTrail.getCommand());
		obj.setId(commandSendTrail.getId());
		obj.setUpdatedOn(commandSendTrail.getUpdatedOn());
		obj.setCreatedOn(commandSendTrail.getCreatedOn());
		obj.setVechileId(commandSendTrail.getVechileId());
		return obj;

	}

}
