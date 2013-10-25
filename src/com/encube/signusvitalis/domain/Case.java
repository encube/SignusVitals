package com.encube.signusvitalis.domain;

public class Case {
	private String caseId;
	private String name;
	private String diagnosis;
	private String date;
	private int bedNumber;
	private boolean normal;
	private boolean monitored;
	private int wardId;

	public Case() {
		this.caseId = "none";
		this.name = "none";
		this.diagnosis = "none";
		this.date = "none";
		this.bedNumber = -1;
		normal = false;
		monitored = false;
	}

	public Case(String caseId, String name, String diagnosis, String date,
			int bedNumber, boolean monitor, boolean normal, int wardId) {
		this.caseId = caseId;
		this.name = name;
		this.diagnosis = diagnosis;
		this.date = date;
		this.bedNumber = bedNumber;
		this.normal = normal;
		this.monitored = monitor;
		this.wardId = wardId;

	}

	public String getCaseId() {
		return caseId;
	}

	public String getName() {
		return name;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public String getDate() {
		return date;
	}

	public boolean isNormal() {
		return normal;
	}

	public boolean isMonitored() {
		return monitored;
	}

	public int getBedNumber() {
		return bedNumber;
	}

	public int getWardId() {
		return this.wardId;
	}
}
