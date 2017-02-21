package com.gaoxiaobang;

public class Output {
	private String compilerInfo;
	private String content;
	private String error;
	private JUnitResult testResults;
	private int status;
	private int code;

	public Output() {
		
	}
	
	public Output(String compilerInfo, String content, String error, JUnitResult testResults, int status, int code) {
		this.compilerInfo = compilerInfo;
		this.content = content;
		this.error = error;
		this.testResults = testResults;
		this.status = status;
		this.code = code;
	}
	
	public void setCompilerInfo(String compilerInfo) {
		this.compilerInfo = compilerInfo;
	}

	public String getCompilerInfo() {
		return compilerInfo;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
	
	public void setError(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}
	
	public void setTestResults(JUnitResult testResults) {
		this.testResults = testResults;
	}

	public JUnitResult getTestResults() {
		return testResults;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
	
	public void setCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
