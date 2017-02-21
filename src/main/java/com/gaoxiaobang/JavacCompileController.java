package com.gaoxiaobang;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

@Controller
@RequestMapping("/api/compile")
public class JavacCompileController {
	
	private final String invokeMainFileName = "invokeMain.sh";

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody Output compile(@RequestBody() Input input) throws Exception {
		long startTime = System.currentTimeMillis();
		
		//Validate input parameters
		if (input.getUserName().isEmpty()) {
			throw new Exception("the userName field should not be empty");
		}
		if (input.getToken().isEmpty()) {
			throw new Exception("the token field should not be empty");
		}
		if (input.getTimestamp() == 0) {
			throw new Exception("the timestamp field should be provided");
		}
		if (input.getLangId() != 10) {
			throw new Exception("the langId field is invalid");
		}
		if (input.getRand().isEmpty()) {
			throw new Exception("the rand field should not be empty");
		}	
		if (input.getBody().length == 0) {
			throw new Exception("the body filed should not be empty");
		}
		if (input.getMainPath().isEmpty()) {
			throw new Exception("the mainPath field should not be empty");
		}
		if (input.getUserName().length() > 40) {
			throw new Exception("the maximum allowd userName length is 40");
		}
		if (input.getMainPath().indexOf(".java") != input.getMainPath().length()-5) {
			throw new Exception("the mainPath field should be ended with .java");
		}
		
		//check token
		String str = input.getTimestamp() + "&" + input.getRand() + "&" + input.getLangId();
		System.out.println("str: " + str);
		MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes());
        byte byteData[] = md.digest();

        //convert the byte to hex format
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        
        System.out.println("sb: " + sb.toString());
        if (input.getToken().compareTo(sb.toString()) != 0) {
        	throw new Exception("the token is invalid");
        }
		
		String userName = input.getUserName();
		
		// create user directory
		File userDir = new File(userName);
		if (userDir.exists()) {
			try {
				FileUtils.deleteDirectory(userDir);
			} catch (IOException e) {
				e.printStackTrace();
				throw(e);
			}
		}
		userDir.mkdirs();

		// Create java files
		CodeFile[][] fileGroups = new CodeFile[2][];
		fileGroups[0] = input.getBody();
		fileGroups[1] = input.getTestCase();
		File[] sourceFiles = new File[fileGroups[0].length + fileGroups[1].length];
		int index = 0;
		for (int i = 0; i < fileGroups.length; i++) {
			CodeFile[] fileGroup = fileGroups[i];
			String parentDir = "src/";
			if (i == 1) {
				parentDir = "test/";
			}
			for (int j = 0; j < fileGroup.length; j++) {
				try {
					File file = new File(userDir, parentDir + fileGroup[j].getPath());
					File fileDir = new File(file.getParent());
					fileDir.mkdirs();
					FileUtils.writeStringToFile(file, fileGroup[j].getContent());
					sourceFiles[index++] = file;
				} catch (IOException e) {
					e.printStackTrace();
					throw(e);
				}
			}
		}
		
		long createFileTime = System.currentTimeMillis();
		System.out.println(String.format("********time-create-file: %d", createFileTime-startTime));
		
		// build the project	
        /*ExecuteShellComand execCmd = new ExecuteShellComand(); 
        String mainPath = input.getMainPath();
        String command = "javac -cp ./" + userName + "/src " + userName + "/src/" + mainPath;
        System.out.println(command);
        ExecCommandResult buildResult = execCmd.executeCommand(command);  
        System.out.println("build inputInfo: " + buildResult.getInputInfo());
        System.out.println("build erroInfo: " + buildResult.getErrorInfo());*/
		
		//build the project
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
		
		Iterable<? extends JavaFileObject> compilationUnits1 = fileManager
				.getJavaFileObjects(sourceFiles);
		List<String> options = new ArrayList<String>();
        options.add("-classpath");
        options.add("./cloud-compile-lib/junit.jar:./cloud-compile-lib/org.hamcrest.core_1.3.0.v201303031735.jar");
        compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits1).call();
		String compilerInfo = "";
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
        	compilerInfo = compilerInfo + diagnostic.toString();
        }
        if (!compilerInfo.isEmpty()) {
        	System.out.println("compilerInfo: " + compilerInfo);
        }
        
        fileManager.close();
        if (!compilerInfo.isEmpty()) {
            return new Output(compilerInfo, "", "", null, 200, 1);
        }
        
        long buildTime = System.currentTimeMillis();
		System.out.println(String.format("********time-build: %d", buildTime-createFileTime));
		
		// execute the program
        String args = " ";
        if (!input.getMainArgus().isEmpty()) {
        	args += input.getMainArgus();
        }
        String mainPath = input.getMainPath();
        String mainClass = mainPath.substring(0, mainPath.length()-5);
        mainClass = mainClass.replaceAll("/", ".");
        String shellContent = "java -classpath " + userName + "/src " + mainClass + args;
        if (!input.getRuntimeArgus().isEmpty()) {
        	shellContent = "echo '" + input.getRuntimeArgus() + "' | java -classpath " + userName + "/src " + mainClass + args;
        }
        try {
			File file = new File(userDir, invokeMainFileName);
			FileUtils.writeStringToFile(file, shellContent);
		} catch (IOException e) {
			e.printStackTrace();
			throw(e);
		}
        String command = "sh ./" + userName + "/" + invokeMainFileName;
        System.out.println(command);
        ExecuteShellComand execCmd = new ExecuteShellComand(); 
        ExecCommandResult runResult = execCmd.executeCommand(command, input.getTimeout());  
        if (!runResult.getErrorInfo().isEmpty()) {
        	System.out.println("execution error: " + runResult.getErrorInfo());
        }
        if (!runResult.getInputInfo().isEmpty()) {
        	System.out.println("execution result: " + runResult.getInputInfo());
        }
        
        long executeTime = System.currentTimeMillis();
		System.out.println(String.format("********time-execute: %d", executeTime-buildTime));
        
        JUnitResult testResult = null;
		if (input.getTestCase().length > 0) {
			ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();

			// Add the src and test dir to the classpath
			// Chain the current thread classloader
			URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { new File(userName + "/src").toURI().toURL(),
					new File(userName + "/test").toURI().toURL() }, currentThreadClassLoader);

			CodeFile[] tests = input.getTestCase();
			JUnitCore junit = new JUnitCore();

			StringBuffer testFiles = new StringBuffer();
			for (int i = 0; i < tests.length; i++) {
				testFiles.append(String.format(" ./%s/test/%s", userName, tests[i].getPath()));
			}

			// build the test class
			/*command = String.format(
					"javac -cp ./%s/src:./%s/test:./cloud-compile-lib/junit.jar:./cloud-compile-lib/org.hamcrest.core_1.3.0.v201303031735.jar %s",
					userName, userName, testFiles);
			System.out.println(command);
			buildResult = execCmd.executeCommand(command);
			System.out.println("build testFiles" + " inputInfo: " + buildResult.getInputInfo());
			System.out.println("build testFiles" + " erroInfo: " + buildResult.getErrorInfo());*/

			Class<?>[] testClasses = new Class<?>[tests.length];
			for (int i = 0; i < tests.length; i++) {
				String testClass = tests[i].getPath();
				testClass = testClass.substring(0, testClass.length() - 5);
				testClass = testClass.replaceAll("/", ".");
				Class<?> cls = Class.forName(testClass, true, urlClassLoader);
				testClasses[i] = cls;
			}

			// execute the test class
			Result result = junit.run(testClasses);
			System.out.println("RunCount: " + result.getRunCount());
			System.out.println("IgnoreCount: " + result.getIgnoreCount());
			System.out.println("FailureCount: " + result.getFailureCount());
			System.out.println("RunTime: " + result.getRunTime());

			ArrayList<JUnitFailure> failures = new ArrayList<JUnitFailure>();
			List<Failure> fas = result.getFailures();
			int k = 0;
			for (Failure fa : fas) {
				System.out.println("Failure-" + k++);
				System.out.println("   TestHeader: " + fa.getTestHeader());
				System.out.println("   Message: " + fa.getMessage());
				failures.add(new JUnitFailure(fa.getTestHeader(), fa.getMessage()));
			}

			testResult = new JUnitResult(result.getRunCount(), result.getIgnoreCount(), result.getFailureCount(),
					result.getRunTime(), failures);
		}
		
		long testTime = System.currentTimeMillis();
		System.out.println(String.format("********time-test: %d", testTime-executeTime));
        
       
        /*String compilerInfo = buildResult.getInputInfo();
        if (!buildResult.getErrorInfo().isEmpty()) {
        	compilerInfo = buildResult.getErrorInfo();
        }*/
		int code = 0;
		if (!runResult.getErrorInfo().isEmpty()) {
			code = 1;
		}
        Output output = new Output(compilerInfo, runResult.getInputInfo(), 
        		runResult.getErrorInfo(), testResult, 200, code);
        
        long totalTime = System.currentTimeMillis();
        System.out.println(String.format("********time-total: %d", totalTime-startTime));
        
        return output;
	}  
}
