package domain.coverage.instrument;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import main.activator.Activator;

import org.eclipse.jface.preference.IPreferenceStore;

import ui.constants.Preferences;

public class BytemanRunner {
	
	public void run(String path, String className, String classPath) {
		try {
			IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
			String byteman = preferenceStore.getString(Preferences.BYTEMAN_PATH);
			String cmd = "java -javaagent:" + byteman + "/lib/byteman.jar=script:" + path + " -cp " + classPath + " " + className;
			Process p = Runtime.getRuntime().exec(cmd);  
	        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));  
	        String line = null;  
	        while ((line = in.readLine()) != null) {  
	            System.out.println(line);  
	        }
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } 
	}

}
