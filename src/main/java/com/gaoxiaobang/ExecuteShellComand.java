package com.gaoxiaobang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ExecuteShellComand {
	ExecCommandResult executeCommand(String command, long timeout) throws IOException, TimeoutException, InterruptedException {
		StringBuffer inputBuf = new StringBuffer();
		StringBuffer errorBuf = new StringBuffer();
		ExecCommandResult result = new ExecCommandResult();
		Process p;
		p = Runtime.getRuntime().exec(command);
		if (timeout == 0) {
			// default timeout value is 20s
			timeout = 20;
		}
		if (!p.waitFor(timeout, TimeUnit.SECONDS)) {
			// timeout - kill the process.
			p.destroy(); // consider using destroyForcibly instead
			throw new TimeoutException();
		}
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		String line = "";
		while ((line = inputReader.readLine()) != null) {
			inputBuf.append(line + "\n");
		}

		while ((line = errorReader.readLine()) != null) {
			errorBuf.append(line + "\n");
		}

		result.setInputInfo(inputBuf.toString());
		result.setErrorInfo(errorBuf.toString());

		return result;
	}
}
