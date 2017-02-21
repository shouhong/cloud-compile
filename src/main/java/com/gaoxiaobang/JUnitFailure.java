package com.gaoxiaobang;

public class JUnitFailure {
	private String testHeader;
	private String message;

	public JUnitFailure() {
		
	}
	
	public JUnitFailure(String testHeader, String message) {
		this.testHeader = testHeader;
		this.message = message;
	}
	
	public void setTestHeader(String testHeader) {
		this.testHeader = testHeader;
	}

	public String getTestHeader() {
		return testHeader;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
