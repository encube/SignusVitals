package com.encube.signusvitalis.exception;

public class IncorrectOldPassword extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "old password mismatched";
	}

}
