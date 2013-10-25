package com.encube.signusvitalis.domain;

public class Patient {
	private String caseId;
	private String patientName;
	private String bedNumber;

	public Patient(String caseId, String patientName, String bedNumber) {
		this.caseId = caseId;
		this.patientName = patientName;
		this.bedNumber = bedNumber;
	}

	public String getCaseId() {
		return caseId;
	}

	public String getPatientName() {
		return patientName;
	}

	public String getBedNumber() {
		return bedNumber;
	}

}
