package com.gaoxiaobang;

public class ExecCommandResult {
	private String inputInfo;
	private String erroInfo;

	public ExecCommandResult() {
		
	}
	
	public ExecCommandResult(String inputInfo, String erroInfo) {
		this.inputInfo = inputInfo;
		this.erroInfo = erroInfo;
	}
	
	public void setInputInfo(String inputInfo) {
		this.inputInfo = inputInfo;
	}

	public String getInputInfo() {
		return inputInfo;
	}
	
	public void setErrorInfo(String erroInfo) {
		this.erroInfo = erroInfo;
	}

	public String getErrorInfo() {
		return erroInfo;
	}
}
