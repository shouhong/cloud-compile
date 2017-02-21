package com.gaoxiaobang;

import java.util.ArrayList;

public class JUnitResult {
	private int runCount = 0;
	private int ignoreCount = 0;
	private int failureCount = 0;
	private long runTime = 0;
	private ArrayList<JUnitFailure> failures = new ArrayList<JUnitFailure>();

	public JUnitResult() {
	}
	
	public JUnitResult(int runCount, int ignoreCount, int failureCount, 
			long runTime, ArrayList<JUnitFailure> failures) {
		this.runCount = runCount;
		this.ignoreCount = ignoreCount;
		this.failureCount = failureCount;
		this.runTime = runTime;
		this.failures = failures;
	}
	
	public void setRunCount(int runCount) {
		this.runCount = runCount;
	}

	public int getRunCount() {
		return runCount;
	}
	
	public void setIgnoreCount(int ignoreCount) {
		this.ignoreCount = ignoreCount;
	}

	public int getIgnoreCount() {
		return ignoreCount;
	}
	
	public void setRunTime(long runTime) {
		this.runTime = runTime;
	}

	public long getRunTime() {
		return runTime;
	}
	
	public void setFailureCount(int failureCount) {
		this.failureCount = failureCount;
	}

	public int getFailureCount() {
		return failureCount;
	}
	
	public void setFailures(ArrayList<JUnitFailure> failures) {
		this.failures = failures;
	}

	public ArrayList<JUnitFailure> getFailures() {
		return failures;
	}
}
