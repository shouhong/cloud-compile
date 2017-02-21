package com.gaoxiaobang;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;

@Controller
@RequestMapping("/api/compile/maven")
public class MavenCompileController {

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody Output sayHello(@RequestBody() Input input) throws IOException, TimeoutException, InterruptedException {
		
		String userName = "user01";
		
		// create user directory
		File userDir = new File(userName);
		if (userDir.exists()) {
			try {
				FileUtils.deleteDirectory(userDir);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		userDir.mkdirs();

		// copy pom.xml to user directory
		File sourceDir = new File("pom");
		File source = new File(sourceDir, "pom.xml");
		File dest = new File(userDir, "pom.xml");
		try {
			FileUtils.copyFile(source, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Create java files
		CodeFile[] body = input.getBody();
		for (int i = 0; i < body.length; i++) {
			try {
				File file = new File(userDir, "src/" + body[i].getPath());
				File fileDir = new File(file.getParent());
				fileDir.mkdirs();
				FileUtils.writeStringToFile(file, body[i].getContent());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// build the project	
        ExecuteShellComand execCmd = new ExecuteShellComand(); 
        String command = "//usr//local//bin//mvn -f " + userName + " package";  
        System.out.println(command);
        ExecCommandResult buildResult = execCmd.executeCommand(command, input.getTimeout());  
        System.out.println("build inputInfo: " + buildResult.getInputInfo());
        System.out.println("build erroInfo: " + buildResult.getErrorInfo()); 
		
		// execute the program 
        command = "java -jar " + userName + "//target//project-0.1.0.jar";  
        System.out.println(command);
        ExecCommandResult runResult = execCmd.executeCommand(command, input.getTimeout());  
        System.out.println("run inputInfo: " + runResult.getInputInfo());
        System.out.println("run erroInfo: " + runResult.getErrorInfo());
        
        Output output = new Output();
        String compilerInfo = buildResult.getInputInfo();
        if (!buildResult.getErrorInfo().isEmpty()) {
        	compilerInfo = buildResult.getErrorInfo();
        }
        output.setCompilerInfo(compilerInfo);
        String content = runResult.getInputInfo();
        if (!runResult.getErrorInfo().isEmpty()) {
        	content = runResult.getErrorInfo();
        }
        output.setContent(content);
        
        return output;
	}  
}
