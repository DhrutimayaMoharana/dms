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

	@Column(name = "imei_number")
	private String imeiNumber;

	@Column(name = "vechile_id")
	private Long vechileId;

	@Column(name = "end_command")
	private String endCommand;

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

	public String getImeiNumber() {
		return imeiNumber;
	}

	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}

	public Long getVechileId() {
		return vechileId;
	}

	public void setVechileId(Long vechileId) {
		this.vechileId = vechileId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getEndCommand() {
		return endCommand;
	}

	public void setEndCommand(String endCommand) {
		this.endCommand = endCommand;
	}

	public Command() {
		super();
	}

}
