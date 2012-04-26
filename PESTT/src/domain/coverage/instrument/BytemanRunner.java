package domain.coverage.instrument;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import main.activator.Activator;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import ui.constants.Preferences;
import ui.editor.ActiveEditor;

public class BytemanRunner {
	
	public void run(String path, String className, String classPath) {
		ActiveEditor testEditor = new ActiveEditor();
		Collection<String> methodsToRun = new ArrayList<String>();
		if(testEditor.isInMethod())
			methodsToRun.add(testEditor.getSelectedMethod());
		else
			methodsToRun = testEditor.getMethodNames();
		System.out.println("path " + testEditor.getClassFilePath());

		try {
		/*	Class<?> testClass = Class.forName(testEditor.getLocation());
			Result result = JUnitCore.runClasses(testClass);
			for (Failure failure : result.getFailures()) {
				System.out.println(failure.toString());
			}*/
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			String byteman = preferenceStore.getString(Preferences.BYTEMAN_PATH);
			String cmd = "java -javaagent:" + byteman + "/lib/byteman.jar=script:" + path + " -cp " + classPath + " " + className;
			Process p = Runtime.getRuntime().exec(cmd);  
	        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));  
	        String line = null;  
	        while ((line = in.readLine()) != null) {  
	            System.out.println(line);  
	        }
	        testEditor.deleteObservers();
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } /*catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}*/
	}

}
