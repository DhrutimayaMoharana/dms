package com.watsoo.dms.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "command")
public class Command implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "device_model")
	private String ddevicModelNumber;

	@Column(name = "command")
	private String command;

	@Column(name = "description")
	private String description;

	@OneToOne
	@JoinColumn(name = "command_type_id")
	private CommandType commandType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDdevicModelNumber() {
		return ddevicModelNumber;
	}

	public void setDdevicModelNumber(String ddevicModelNumber) {
		this.ddevicModelNumber = ddevicModelNumber;
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

	public CommandType getCommandType() {
		return commandType;
	}

	public void setCommandType(CommandType commandType) {
		this.commandType = commandType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Command() {
		super();
	}

}
