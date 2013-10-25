package com.encube.signusvitalis.domain;

import java.io.Serializable;

public class VSRecord implements Serializable {

	private static final long serialVersionUID = 5011783273167714412L;
	private String id;
	private String temperature;
	private String pulse;
	private String respiratation;
	private String blood;
	private boolean userEditable;
	private String date;
	private boolean locked;
	private String nurseName;

	public VSRecord(String id, String temperature, String pulse,
			String respiration, String blood, String date, boolean locked,
			boolean userEditable, String nurseName) {
		this.id = id;
		this.temperature = temperature;
		this.pulse = pulse;
		this.respiratation = respiration;
		this.blood = blood;
		this.date = date;
		this.userEditable = userEditable;
		this.locked = locked;
		this.nurseName = nurseName;
	}

	public String getId() {
		return id;
	}

	public String getNurseName() {
		return nurseName;
	}

	public String getTemperature() {
		return temperature;
	}

	public String getPulse() {
		return pulse;
	}

	public String getRespiratation() {
		return respiratation;
	}

	public String getBlood() {
		return blood;
	}

	public String getDate() {
		return date;
	}

	public boolean isLocked() {
		return locked;
	}

	public boolean isUserEditable() {
		return userEditable;
	}
}
