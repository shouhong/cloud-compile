package com.gaoxiaobang;

public class Input {
	private String userName = "";
	private CodeFile[] body = new CodeFile[0];
	private String runtimeArgus = "";
	private String mainArgus = "";
	private String mainPath = "";
	private CodeFile[] testCase = new CodeFile[0];
	private String token = "";
	private long timestamp = 0;
	private int langId = 0;
	private String rand = "";
	private long timeout = 0;
	
	public Input() {
		
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public CodeFile[] getBody() {
		return body;
	}
	
	public void setRuntimeArgus(String runtimeArgus) {
		this.runtimeArgus = runtimeArgus;
	}

	public String getRuntimeArgus() {
		return runtimeArgus;
	}
	
	public void setMainArgus(String mainArgus) {
		this.mainArgus = mainArgus;
	}

	public String getMainArgus() {
		return mainArgus;
	}
	
	public void setMainPath(String mainPath) {
		this.mainPath = mainPath;
	}

	public String getMainPath() {
		return mainPath;
	}
	
	public CodeFile[] getTestCase() {
		return testCase;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getToken() {
		return this.token;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public long getTimestamp() {
		return this.timestamp;
	}
	
	public void setLangId(int langId) {
		this.langId = langId;
	}
	
	public long getLangId() {
		return this.langId;
	}
	
	public void setRand(String rand) {
		this.rand = rand;
	}
	
	public String getRand() {
		return this.rand;
	}
	
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	public long getTimeout() {
		return this.timeout;
	}
}
