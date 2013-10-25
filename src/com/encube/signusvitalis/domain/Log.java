package com.encube.signusvitalis.domain;

public class Log {

	private String id;
	private String text;
	private String time;
	private int wardid;

	public Log(String id, String text, String time, int i) {
		this.id = id;
		this.text = text;
		this.time = time;
		this.wardid = i;
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getTime() {
		return time;
	}

	public int getWardId(){
		return wardid;
	}
}
