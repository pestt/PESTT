package coverage;

import java.lang.reflect.Method;

public class Runner {

	private Class<?> classToRunMethod;
	private String nameMethodToRun;
	
	public void setMethodToRun(Class<?> classToRunMethod, String nameMethodToRun) {
		this.classToRunMethod = classToRunMethod;
		this.nameMethodToRun = nameMethodToRun;
	}
	
	public void run () {
		try {	
			Object objectToRun = classToRunMethod.newInstance();
			Method methodToRun = classToRunMethod.getMethod(nameMethodToRun);
			methodToRun.invoke(objectToRun);
		} catch (Exception e) {
		}
	}
}
