package com.encube.signusvitalis.domain;

public class Ward {
	private int id;
	private String name;

	public Ward(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
